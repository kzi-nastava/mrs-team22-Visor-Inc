import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatButtonModule} from '@angular/material/button';
import {MatExpansionModule} from '@angular/material/expansion';
import {Router} from '@angular/router';
import {FavoriteRoute} from '../favorite-routes';
import {FavoriteRoutesApi} from '../favorite-routes.api';
import {ROUTE_USER_PAGES} from '../../user-pages';

@Component({
  selector: 'app-favorite-route-accordion',
  imports: [MatExpansionModule, MatButtonModule],
  templateUrl: './favorite-routes-accordition.html',
  styleUrls: ['./favorite-routes-accordition.css'],
})
export class FavoriteRouteAccordion {
  @Input({ required: true }) route!: FavoriteRoute;

  @Output() deleted = new EventEmitter<void>();

  deleting = false;

  constructor(private router: Router, private api: FavoriteRoutesApi) {}

  pickRoute(route: FavoriteRoute) {
    this.router.navigate([ROUTE_USER_PAGES], {
      state: {
        favoriteRoute: route.dto,
      },
    });
  }

  removeRoute() {
    if (this.deleting) return;

    this.deleting = true;
    this.api.deleteFavoriteRoute(this.route.id).subscribe({
      next: () => {
        this.deleting = false;
        this.deleted.emit();
      },
      error: () => {
        this.deleting = false;
      },
    });
  }
}
