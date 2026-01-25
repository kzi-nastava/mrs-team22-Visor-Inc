import {
  AfterViewInit,
  Component,
  Output,
  EventEmitter,
  Input,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import { forkJoin, map, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import * as L from 'leaflet';

const NOVI_SAD_BOUNDS = {
  latMin: 45.2,
  latMax: 45.35,
  lonMin: 19.7,
  lonMax: 19.95,
};

type DriverStatus = 'AVAILABLE' | 'GOING_TO_PICKUP' | 'WAITING_AT_PICKUP' | 'IN_RIDE' | null;

type Driver = {
  id: number;
  firstName?: string;
  lastName?: string;
  status: DriverStatus;
  marker: L.Marker;

  route?: L.LatLng[];
  routeIndex?: number;
  direction?: 1 | -1;

  progress?: number;
  speed?: number;

  lastPos?: L.LatLng;
  targetPos?: L.LatLng;
  animStart?: number;
  animDuration?: number;
};

@Component({
  selector: 'app-map',
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements AfterViewInit, OnChanges {
  private drawVersion = 0;
  private readonly FOCUSED_ZOOM = 17;

  private map!: L.Map;
  private drivers: Driver[] = [];

  private waypoints: L.LatLng[] = [];
  private markers: L.Marker[] = [];
  private routeLine: L.Polyline | null = null;

  @Input() focusedDriverId: number | null = null;

  @Input() points: {
    lat: number;
    lng: number;
    type: 'PICKUP' | 'STOP' | 'DROPOFF';
    order: number;
  }[] = [];

  @Input() locked = false;

  @Output() mapClick = new EventEmitter<{
    lat: number;
    lng: number;
    address: string;
  }>();

  @Output() cleared = new EventEmitter<void>();

  constructor(private http: HttpClient) {}

  private userMarkerIcon = L.icon({
    iconUrl: 'assets/icons/location.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
  });

  ngAfterViewInit(): void {
    this.startInterpolationLoop();
    
    if (this.map) return; 
    this.map = L.map('map', {
      zoomControl: false,
      attributionControl: false,
    }).setView([45.2396, 19.8227], 14);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(this.map);

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      this.reverseSearch(e.latlng.lat, e.latlng.lng).subscribe((res) => {
        const address = res?.features?.[0]?.properties?.display_name ?? 'Unknown location';

        this.mapClick.emit({
          lat: e.latlng.lat,
          lng: e.latlng.lng,
          address,
        });
      });
    });
  }

  focusDriver(driverId: number, zoom = 16) {
  const d = this.getDriver(driverId);
  if (!d) return;
  const ll = d.marker.getLatLng();
  this.map.setView(ll, zoom, { animate: true });
}

panTo(lat: number, lng: number) {
  this.map.panTo({ lat, lng }, { animate: true });
}


  applyDriverRoute(driverId: number, coords: { lat: number; lng: number }[]) {
    const driver = this.drivers.find((d) => d.id === driverId);
    if (!driver) return;

    driver.route = coords.map((c) => L.latLng(c.lat, c.lng));
    driver.routeIndex = 0;
    driver.direction = 1;

    if (this.focusedDriverId === driver.id) {
      this.focusDriver(driver.id);
    }

    this.startSimulation();
  }

  startSimulation() {
    let lastTime = performance.now();

    const animate = (now: number) => {
      const deltaSec = (now - lastTime) / 1000;
      lastTime = now;

      this.drivers.forEach((driver) => {
        if (!driver.route || driver.route.length < 2) return;

        const dir = driver.direction ?? 1;
        let i = driver.routeIndex ?? 0;
        let p = driver.progress ?? 0;

        const a = driver.route[i];
        const b = driver.route[i + dir];

        if (!b) {
          driver.direction = dir === 1 ? -1 : 1;
          return;
        }

        const segmentDist = a.distanceTo(b);
        const move = (driver.speed ?? 1) * deltaSec;

        p += move / segmentDist;

        if (p >= 1) {
          driver.routeIndex = i + dir;
          driver.progress = 0;

          if (driver.status === 'GOING_TO_PICKUP' && driver.routeIndex >= driver.route.length - 1) {
            driver.status = 'WAITING_AT_PICKUP';
            driver.route = undefined;
            driver.progress = 0;
            return;
          }

          if (driver.routeIndex <= 0 || driver.routeIndex >= driver.route.length - 1) {
            driver.direction = dir === 1 ? -1 : 1;
          }
        } else {
          driver.progress = p;
          const lat = a.lat + (b.lat - a.lat) * p;
          const lng = a.lng + (b.lng - a.lng) * p;
          driver.marker.setLatLng([lat, lng]);

          if (this.focusedDriverId === driver.id) {
            this.map.panTo([lat, lng], { animate: true });
          }
        }
      });

      requestAnimationFrame(animate);
    };

    requestAnimationFrame(animate);
  }

  getDriver(driverId: number): Driver | undefined {
    return this.drivers.find((d) => d.id === driverId);
  }

  getFreeDriversSnapshot(): {
    driverId: number;
    lat: number;
    lng: number;
  }[] {
    return this.drivers
      .filter((d) => d.status === 'AVAILABLE')
      .map((d) => {
        const pos = d.marker.getLatLng();
        return {
          driverId: d.id,
          lat: pos.lat,
          lng: pos.lng,
        };
      });
  }

  addSimulatedDriver(config: {
    id: number;
    firstName: string;
    lastName: string;
    start: { lat: number; lng: number };
    status: DriverStatus;
  }) {
    const icon = L.icon({
      iconUrl:
        config.status === 'AVAILABLE' ? 'assets/icons/free driver.png' : 'assets/icons/busy driver.png',
      iconSize: [30, 30],
      iconAnchor: [15, 30],
    });

    const marker = L.marker([config.start.lat, config.start.lng], { icon })
      .addTo(this.map)
      .bindTooltip(
        `
      <div style="font-size: 13px; line-height: 1.4">
        <strong>Driver #${config.id}</strong><br/>
        ${config.firstName} ${config.lastName}
      </div>
      `,
        {
          direction: 'top',
          offset: [0, -20],
          opacity: 0.9,
          sticky: true,
        }
      );

    this.drivers.push({
      id: config.id,
      firstName: config.firstName,
      lastName: config.lastName,
      status: config.status,
      marker,
      route: [],
      routeIndex: 0,
      direction: 1,
      progress: 0,
      speed: 0.25 + Math.random() * 0.25,
    });
  }

  assignRouteToDriver(driver: any, start: L.LatLng, end: { lat: number; lng: number }) {
    this.getRoute(start, L.latLng(end.lat, end.lng)).subscribe((res) => {
      const coords = res.routes[0].geometry.coordinates;
      driver.route = coords.map((c: number[]) => L.latLng(c[1], c[0]));
      driver.routeIndex = 0;
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['points'] && this.map) {
      this.syncFromPoints();
    }
  }

  private syncFromPoints() {
    this.drawVersion++;

    this.markers.forEach((m) => this.map.removeLayer(m));
    this.markers = [];

    if (this.routeLine) {
      this.map.removeLayer(this.routeLine);
      this.routeLine = null;
    }

    if (!this.points || this.points.length === 0) {
      this.waypoints = [];
      return;
    }

    const sorted = [...this.points].sort((a, b) => a.order - b.order);

    this.waypoints = sorted.map((p) => {
      const ll = L.latLng(p.lat, p.lng);
      const marker = L.marker(ll, { icon: this.userMarkerIcon }).addTo(this.map);
      this.markers.push(marker);
      return ll;
    });

    if (this.waypoints.length >= 2) {
      this.drawRouteOSRM(this.drawVersion);
    }
  }

  private drawRouteOSRM(version: number) {
    if (this.waypoints.length < 2) return;

    const requests = [];
    for (let i = 0; i < this.waypoints.length - 1; i++) {
      requests.push(this.getRoute(this.waypoints[i], this.waypoints[i + 1]));
    }

    forkJoin(requests).subscribe((results) => {
      if (version !== this.drawVersion) return;

      const allPoints: L.LatLng[] = [];

      results.forEach((res) => {
        const coords = res.routes[0].geometry.coordinates;
        coords.forEach((c: number[]) => {
          allPoints.push(L.latLng(c[1], c[0]));
        });
      });

      if (this.routeLine) {
        this.map.removeLayer(this.routeLine);
      }

      this.routeLine = L.polyline(allPoints, {
        color: '#2563eb',
        weight: 4,
        lineJoin: 'round',
      }).addTo(this.map);
    });
  }

  public clearUserRoute() {
    this.markers.forEach((m) => this.map.removeLayer(m));
    this.markers = [];
    this.waypoints = [];

    if (this.routeLine) {
      this.map.removeLayer(this.routeLine);
      this.routeLine = null;
    }

    this.cleared.emit();
  }

  getRoute(from: L.LatLng, to: L.LatLng) {
    return this.http.get<any>(
      `https://router.project-osrm.org/route/v1/driving/` +
        `${from.lng},${from.lat};${to.lng},${to.lat}` +
        `?overview=full&geometries=geojson`
    );
  }

  reverseSearch(lat: number, lon: number): Observable<any> {
    return this.http.get(
      `https://nominatim.openstreetmap.org/reverse?format=geojson&lat=${lat}&lon=${lon}`
    );
  }

  updateDriverPosition(driverId: number, lat: number, lng: number) {
    const driver = this.drivers.find((d) => d.id === driverId);
    if (!driver) return;

    const now = performance.now();
    const target = L.latLng(lat, lng);

    if (!driver.lastPos) {
      driver.marker.setLatLng(target);
      driver.lastPos = target;
      return;
    }

    driver.lastPos = driver.marker.getLatLng();
    driver.targetPos = target;
    driver.animStart = now;
    driver.animDuration = 3000;
  }

  private startInterpolationLoop() {
    const animate = (now: number) => {
      this.drivers.forEach((driver) => {
        if (!driver.targetPos || !driver.animStart || !driver.animDuration) return;

        const t = (now - driver.animStart) / driver.animDuration;
        if (t >= 1) {
          driver.marker.setLatLng(driver.targetPos);
          driver.lastPos = driver.targetPos;
          driver.targetPos = undefined;
          return;
        }

        const a = driver.lastPos!;
        const b = driver.targetPos;

        const lat = a.lat + (b.lat - a.lat) * t;
        const lng = a.lng + (b.lng - a.lng) * t;

        driver.marker.setLatLng([lat, lng]);
      });

      requestAnimationFrame(animate);
    };

    requestAnimationFrame(animate);
  }

  searchAddress(address: string): Observable<{ lat: number; lng: number }> {
  return this.http
    .get<any>(
      `https://nominatim.openstreetmap.org/search` +
        `?format=json&q=${encodeURIComponent(address)}&limit=1`
    )
    .pipe(
      map((res) => ({
        lat: parseFloat(res[0].lat),
        lng: parseFloat(res[0].lon),
      }))
    );
}

drawRouteFromAddresses(startAddress: string, endAddress: string) {
  forkJoin({
    start: this.searchAddress(startAddress),
    end: this.searchAddress(endAddress),
  }).subscribe(({ start, end }) => {
    this.points = [
      { lat: start.lat, lng: start.lng, type: 'PICKUP', order: 0 },
      { lat: end.lat, lng: end.lng, type: 'DROPOFF', order: 1 },
    ];

    this.syncFromPoints(); // reuse existing logic
  });
}


}
