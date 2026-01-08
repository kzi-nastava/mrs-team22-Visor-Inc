import {
  AfterViewInit,
  Component,
  Output,
  EventEmitter,
  Input,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import * as L from 'leaflet';

const NOVI_SAD_BOUNDS = {
  latMin: 45.2,
  latMax: 45.35,
  lonMin: 19.7,
  lonMax: 19.95,
};

type Driver = {
  id: number;
  status: string;
  marker: L.Marker;
};

@Component({
  selector: 'app-map',
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements AfterViewInit, OnChanges {
  private drawVersion = 0;

  private map!: L.Map;
  private drivers: Driver[] = [];

  private waypoints: L.LatLng[] = [];
  private markers: L.Marker[] = [];
  private routeLine: L.Polyline | null = null;

  @Input() points: {
    lat: number;
    lng: number;
    type: 'PICKUP' | 'STOP' | 'DROPOFF';
    order: number;
  }[] = [];

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
    this.map = L.map('map').setView([45.2396, 19.8227], 14);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(this.map);

    const ClearControl = L.Control.extend({
      onAdd: () => {
        const btn = L.DomUtil.create('button', 'leaflet-bar');
        btn.innerHTML = '✕';
        btn.style.width = '36px';
        btn.style.height = '36px';
        btn.style.cursor = 'pointer';
        btn.style.fontSize = '20px';
        btn.style.background = 'white';
        btn.style.border = 'none';

        btn.onclick = (e) => {
          e.preventDefault();
          e.stopPropagation();
          this.clearUserRoute();
        };

        return btn;
      },
    });

    this.map.addControl(new ClearControl({ position: 'topright' }));

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

    this.initDrivers();
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

  private clearUserRoute() {
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

  private randomLatLng(): L.LatLng {
    const lat =
      NOVI_SAD_BOUNDS.latMin + Math.random() * (NOVI_SAD_BOUNDS.latMax - NOVI_SAD_BOUNDS.latMin);
    const lon =
      NOVI_SAD_BOUNDS.lonMin + Math.random() * (NOVI_SAD_BOUNDS.lonMax - NOVI_SAD_BOUNDS.lonMin);
    return new L.LatLng(lat, lon);
  }

  private async initDrivers() {
    for (let i = 0; i < 10; i++) {
      const status = i < 5 ? 'BUSY' : 'FREE';
      const startPos = this.randomLatLng();

      const icon = L.icon({
        iconUrl:
          status === 'BUSY' ? 'assets/icons/busy driver.png' : 'assets/icons/free driver.png',
        iconSize: [30, 30],
        iconAnchor: [15, 30],
      });

      const marker = L.marker(startPos, { icon }).addTo(this.map);

      this.drivers.push({
        id: i,
        status,
        marker,
      });
    }
  }
}
