import { Component, Input } from '@angular/core';
import { MatButton, MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';

export interface FavoriteRoute {
  id: number;
  name: string;
  start: string;
  end: string;
  distanceKm: number;
  stops: string[];
}

@Component({
  selector: 'app-favorite-route-accordion',
  imports: [MatExpansionModule, MatButtonModule],
  templateUrl: './favorite-routes-accordition.html',
  styleUrls: ['./favorite-routes-accordition.css'],
})
export class FavoriteRouteAccordion {
  @Input({ required: true }) route!: FavoriteRoute;

  pickRoute() {
    console.log('Pick route', this.route);
  }

  removeRoute() {
    console.log('Remove route', this.route);
  }
}
