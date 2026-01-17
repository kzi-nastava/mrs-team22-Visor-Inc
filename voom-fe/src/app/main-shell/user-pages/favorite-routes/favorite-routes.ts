import {Component, OnInit} from '@angular/core';
import {FavoriteRouteAccordion} from './favorite-routes-accordition/favorite-routes-accordition';
import {FavoriteRouteDto, FavoriteRoutesApi} from './favorite-routes.api';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';

export const ROUTE_FAVORITE_ROUTES = 'favorite';

export interface FavoriteRoute {
  dto: FavoriteRouteDto;
  id: number;
  name: string;
  start: string;
  end: string;
  distanceKm: number;
  stops: string[];
}

@Component({
  selector: 'app-favorite-routes',
  imports: [FavoriteRouteAccordion, MatSnackBarModule],
  templateUrl: './favorite-routes.html',
  styleUrl: './favorite-routes.css',
})
export class FavoriteRoutes implements OnInit {
  routes: FavoriteRoute[] = [];
  loading = true;

  constructor(private api: FavoriteRoutesApi, private snackBar: MatSnackBar) {}

  ngOnInit() {
    this.api.getFavoriteRoutes().subscribe({
      next: (data) => {
        this.routes = data.map(this.mapDto);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  private mapDto(dto: any): FavoriteRoute {
    const pickup = dto.points.find((p: any) => p.type === 'PICKUP');
    const dropoff = dto.points.find((p: any) => p.type === 'DROPOFF');

    return {
      dto: dto,
      id: dto.id,
      name: dto.name,
      start: pickup?.address ?? '',
      end: dropoff?.address ?? '',
      distanceKm: dto.totalDistanceKm,
      stops: dto.points.filter((p: any) => p.type === 'STOP').map((p: any) => p.address),
    };
  }

  fetch() {
    this.loading = true;

    this.api.getFavoriteRoutes().subscribe({
      next: (data) => {
        this.routes = data.map(this.mapDto);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  onRouteDeleted() {
    this.snackBar.open('Favorite route successfully removed', 'Close', {
      duration: 3000,
    });
    this.fetch();
  }
}
