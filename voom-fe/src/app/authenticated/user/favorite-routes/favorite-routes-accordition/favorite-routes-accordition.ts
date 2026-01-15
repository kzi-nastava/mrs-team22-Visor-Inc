import { Component, Input } from '@angular/core';
import { MatButton, MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { Router } from '@angular/router';
import { FavoriteRoute } from '../favorite-routes';


@Component({
  selector: 'app-favorite-route-accordion',
  imports: [MatExpansionModule, MatButtonModule],
  templateUrl: './favorite-routes-accordition.html',
  styleUrls: ['./favorite-routes-accordition.css'],
})
export class FavoriteRouteAccordion {
  @Input({ required: true }) route!: FavoriteRoute;

  constructor(private router: Router) {}

  pickRoute(route: FavoriteRoute) {
    this.router.navigate(['/user/home'], {
      state: {
        favoriteRoute: route.dto,
      },
    });
  }

  removeRoute() {
    console.log('Remove route', this.route);
  }
}
