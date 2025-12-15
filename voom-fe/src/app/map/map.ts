import { AfterViewInit, Component } from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import { GeocodingService } from '../services/geocoding.service';
import { RideForm } from '../ride-form/ride-form';

@Component({
  selector: 'app-map',
  imports: [
    RideForm
  ],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map implements AfterViewInit {
  private waypoints: L.LatLng[] = [];
  private clicks: number = 0;

  constructor(private geocodingService: GeocodingService) {}

  ngAfterViewInit(): void {
    const map = L.map('map').setView([45.2396, 19.8227], 14);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(map);

    // const icon: L.Icon = new L.Icon({
    //   iconUrl: 'm',
    // });

    const onClick = (e: L.LeafletMouseEvent): void => {
      if (this.clicks >= 2) {
        return;
      }
      this.waypoints.push(e.latlng);
      this.clicks++;

      const marker: L.Marker = new L.Marker(e.latlng);
      marker.addTo(map);

      if (this.clicks === 2) {
        L.Routing.control({
          waypoints: this.waypoints,
          router: L.Routing.osrmv1({
            serviceUrl: 'https://routing.openstreetmap.de/routed-foot/route/v1',
          }),
        }).addTo(map);
      }
    };

    map.on('click', onClick);
  }
}
