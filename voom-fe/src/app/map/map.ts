import { afterNextRender, Component } from '@angular/core';

@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.html',
  styleUrl: './map.css',
})
export class Map {
  private map?: any;

  private async initMap(): Promise<void> {
    const L = await import('leaflet');
    this.map = L.map('map').setView([45.2396, 19.8227], 14);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(this.map);
  }

  constructor() {
    afterNextRender(() => {
      this.initMap();
    });
  }
}
