import { AfterViewInit, Component, Output, EventEmitter } from '@angular/core';
import { forkJoin, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import * as L from 'leaflet';
import 'leaflet-routing-machine';

@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements AfterViewInit {
  private waypoints: L.LatLng[] = [];
  private clicks: number = 0;

  constructor(private http: HttpClient) {}

  @Output() routeCalculated = new EventEmitter<RouteSummary>();

  private getEtaDistance(): number {
    return 0;
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
    const map = L.map('map').setView([45.2396, 19.8227], 14);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(map);

    const onClick = (e: L.LeafletMouseEvent): void => {
      // HARDCODED AT 2 FOR UNREGISTERED USER CUZ THEY CANT ADD MORE WAYPOINTS, MUST BE MODIFIED FOR REGISTERED USERS
      if (this.clicks >= 2) {
        return;
      }
      this.waypoints.push(e.latlng);
      this.clicks++;

      const marker: L.Marker = new L.Marker(e.latlng);
      marker.addTo(map);

      if (this.clicks === 2) {
        const control: L.Routing.Control = L.Routing.control({
          waypoints: this.waypoints,
          router: L.Routing.osrmv1({
            serviceUrl: 'https://routing.openstreetmap.de/routed-foot/route/v1',
          }),
        }).addTo(map);

        control.on('routesfound', (e: L.Routing.RoutingResultEvent) => {
          const routes: L.Routing.IRoute[] = e.routes;
          if (routes && routes.length > 0) {
            const bestRoute = routes[0];
            console.log(bestRoute.summary);
            if (bestRoute.summary) {
              let distanceKilometers = parseFloat(
                (bestRoute.summary.totalDistance / 1000).toFixed(2)
              );
              let time = bestRoute.summary.totalTime / 60;
              let sourceCoords = this.waypoints.at(0);
              let destCoords = this.waypoints.at(-1);

              if (sourceCoords && destCoords) {
                forkJoin({
                  sourceRes: this.reverseSearch(sourceCoords.lat, sourceCoords.lng),
                  destinationRes: this.reverseSearch(destCoords.lat, destCoords.lng),
                }).subscribe(({ sourceRes, destinationRes }) => {
                  console.log(sourceRes);
                  console.log(destinationRes);
                  let sourceAddressObj = sourceRes.features[0].properties.geocoding;
                  let destAddressObj = destinationRes.features[0].properties.geocoding;
                  let sourceAddressString = `${sourceAddressObj.street} ${
                    sourceAddressObj.housenumber || ''
                  }, ${sourceAddressObj.city}`;
                  let destAddressString = `${destAddressObj.street} ${
                    destAddressObj.housenumber || ''
                  }, ${destAddressObj.city}`;
                  const rideSummary = new RouteSummary(
                    distanceKilometers,
                    time,
                    sourceCoords,
                    destCoords,
                    sourceAddressString,
                    destAddressString
                  );

                  this.routeCalculated.emit(rideSummary);
                });
              }
            }
          }
        });
      }
    };

    map.on('click', onClick);
  }
}

export class RouteSummary {
  constructor(
    public distance: number,
    public time: number,
    public sourceCoords: L.LatLng,
    public destinationCoords: L.LatLng,
    public sourceAddress: string,
    public destinationAddress: string
  ) {}
}
