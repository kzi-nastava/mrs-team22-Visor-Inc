import { AfterViewInit, Component, Output, EventEmitter } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import * as L from 'leaflet';
// import 'leaflet-routing-machine';

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
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements AfterViewInit {
  private waypoints: L.LatLng[] = [];
  constructor(private http: HttpClient) {}

  private map: any = null;
  private drivers: Driver[] = [];

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
      const freeDriverIcon = L.icon({
        iconUrl: 'assets/icons/free driver.png',
        iconSize: [30, 30],
        iconAnchor: [30, 30],
      });

      const busyDriverIcon = L.icon({
        iconUrl: 'assets/icons/busy driver.png',
        iconSize: [30, 30],
        iconAnchor: [30, 30],
      });
      const options = {
        icon: status === 'BUSY' ? busyDriverIcon : freeDriverIcon,
      };
      const marker = L.marker(startPos, options).addTo(this.map);
      marker.on('click', () => {
        marker.bindPopup(`<b>Driver ${driver.id}</b><br>Status: ${driver.status}`).openPopup();
      });

      let driver = {
        id: i,
        status: status,
        marker: marker,
        routeIndex: 0,
        speed: 1,
      };

      this.snapToRoad(startPos).subscribe((res) => {
        const snapped = res.waypoints[0].location;
        driver.marker.setLatLng([snapped[1], snapped[0]]);
      });

      if (status === 'BUSY') {
        this.assignRouteToDriver(driver, startPos);
      }

      this.drivers.push(driver);
    }
  }

  assignRouteToDriver(driver: Driver, start: L.LatLng) {
    const end = this.randomLatLng();

    this.getRoute(start, end).subscribe((res) => {
      const coords = res.routes[0].geometry.coordinates;
      console.log(`from getRoute: ${coords}`);
      driver.route = coords.map((c: number[]) => L.latLng(c[1], c[0]));

      driver.routeIndex = 0;
    });
  }

  snapToRoad(latlng: L.LatLng) {
    return this.http.get<any>(
      `https://router.project-osrm.org/nearest/v1/driving/${latlng.lng},${latlng.lat}`
    );
  }

  getRoute(from: L.LatLng, to: L.LatLng) {
    return this.http.get<any>(
      `https://router.project-osrm.org/route/v1/driving/` +
        `${from.lng},${from.lat};${to.lng},${to.lat}` +
        `?overview=full&geometries=geojson`
    );
  }

  startSimulation() {
    console.log(this.drivers);
    console.log('sim started');
    setInterval(() => {
      this.drivers
        .filter((d) => d.status === 'BUSY' && d.route)
        .forEach((driver) => {
          console.log(driver);
          const i = driver.routeIndex!;
          if (i >= driver.route!.length - 1) {
            driver.routeIndex = 0;
            return;
          }

          const curr = driver.route![i];
          const next = driver.route![i + 1];

          const t = 0.000001;

          const lat = this.lerp(curr.lat, next.lat, t);
          const lng = this.lerp(curr.lng, next.lng, t);

          driver.marker.setLatLng([lat, lng]);

          console.log(`new: ${lat} ${lng}`);

          driver.routeIndex! += driver.speed!;
        });
    }, 1000);
  }

  lerp(a: number, b: number, t: number) {
    return a + (b - a) * t;
  }

  searchStreet(street: string): Observable<any> {
    return this.http.get('https://nominatim.openstreetmap.org/search?format=json&q=' + street);
  }
  reverseSearch(lat: number, lon: number): Observable<any> {
    return this.http.get(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&addressdetails=1&format=geocodejson`
    );
  }
  ngAfterViewInit(): void {
    this.map = L.map('map').setView([45.2396, 19.8227], 14);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(this.map);

    this.initDrivers();
    this.startSimulation();
  }
}
