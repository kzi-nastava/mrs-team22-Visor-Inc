import { AfterViewInit, Component, Output, EventEmitter } from '@angular/core';
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
  route?: L.LatLng[];
  routeIndex?: number;
  speed?: number;
};

@Component({
  selector: 'app-map',
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements AfterViewInit {
  private map!: L.Map;
  private drivers: Driver[] = [];

  private waypoints: L.LatLng[] = [];
  private markers: L.Marker[] = [];
  private routeLine: L.Polyline | null = null;

  @Output() mapClick = new EventEmitter<{
    lat: number;
    lng: number;
    address: string;
  }>();

  constructor(private http: HttpClient) {}

  private userMarkerIcon = L.icon({
    iconUrl: 'assets/icons/location.png',
    iconSize: [32, 32],
    iconAnchor: [16, 32],
  });

  private randomLatLng(): L.LatLng {
    const lat =
      NOVI_SAD_BOUNDS.latMin + Math.random() * (NOVI_SAD_BOUNDS.latMax - NOVI_SAD_BOUNDS.latMin);
    const lon =
      NOVI_SAD_BOUNDS.lonMin + Math.random() * (NOVI_SAD_BOUNDS.lonMax - NOVI_SAD_BOUNDS.lonMin);
    return new L.LatLng(lat, lon);
  }

  private drawRouteOSRM() {
    if (this.routeLine) {
      this.map.removeLayer(this.routeLine);
      this.routeLine = null;
    }

    if (this.waypoints.length < 2) return;

    const requests = [];
    for (let i = 0; i < this.waypoints.length - 1; i++) {
      requests.push(this.getRoute(this.waypoints[i], this.waypoints[i + 1]));
    }

    forkJoin(requests).subscribe((results) => {
      const allPoints: L.LatLng[] = [];

      results.forEach((res) => {
        const coords = res.routes[0].geometry.coordinates;
        coords.forEach((c: number[]) => {
          allPoints.push(L.latLng(c[1], c[0]));
        });
      });

      this.routeLine = L.polyline(allPoints, {
        color: '#2563eb',
        weight: 4,
        lineJoin: 'round',
      }).addTo(this.map);
    });
  }

  getRoute(from: L.LatLng, to: L.LatLng) {
    return this.http.get<any>(
      `https://router.project-osrm.org/route/v1/driving/` +
        `${from.lng},${from.lat};${to.lng},${to.lat}` +
        `?overview=full&geometries=geojson`
    );
  }

  snapToRoad(latlng: L.LatLng) {
    return this.http.get<any>(
      `https://router.project-osrm.org/nearest/v1/driving/${latlng.lng},${latlng.lat}`
    );
  }

  reverseSearch(lat: number, lon: number): Observable<any> {
    return this.http.get(
      `https://nominatim.openstreetmap.org/reverse?format=geojson&lat=${lat}&lon=${lon}`
    );
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

      const driver = {
        id: i,
        status,
        marker,
        routeIndex: 0,
        speed: 1,
      };

      this.drivers.push(driver);
    }
  }

  ngAfterViewInit(): void {
    this.map = L.map('map').setView([45.2396, 19.8227], 14);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(this.map);

    this.map.on('click', (e: L.LeafletMouseEvent) => {
      const latlng = e.latlng;

      this.reverseSearch(latlng.lat, latlng.lng).subscribe((res) => {
        const address = res?.features?.[0]?.properties?.display_name ?? 'Unknown location';

        const marker = L.marker(latlng, {
          icon: this.userMarkerIcon,
        }).addTo(this.map);

        marker.bindPopup(address);

        this.markers.push(marker);
        this.waypoints.push(latlng);

        this.mapClick.emit({
          lat: latlng.lat,
          lng: latlng.lng,
          address,
        });

        this.drawRouteOSRM();
      });
    });

    this.initDrivers();
  }
}
