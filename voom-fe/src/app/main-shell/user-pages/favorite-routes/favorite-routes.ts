import { Component, OnInit, signal } from '@angular/core';
import { FavoriteRouteAccordion } from './favorite-routes-accordition/favorite-routes-accordition';
import { FavoriteRouteDto, FavoriteRoutesApi } from './favorite-routes.api';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

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
  routes = signal<FavoriteRoute[]>([]);
  loading = signal(true);

  constructor(
    private api: FavoriteRoutesApi,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit() {
    this.api.getFavoriteRoutes().subscribe({
      next: (res) => {
        const mapped = res.data?.map((dto) => this.mapDto(dto)) || [];
        this.routes.set(mapped);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      },
    });
  }

  private shortAddress(address?: string | null): string {
    if (!address) return '';
    const parts = address.split(',');
    return parts.slice(0, 2).join(',').trim();
  }

  private mapDto(dto: FavoriteRouteDto): FavoriteRoute {
    const pickup = dto.points.find((p) => p.type === 'PICKUP');
    const dropoff = dto.points.find((p) => p.type === 'DROPOFF');

    return {
      dto,
      id: dto.id,
      name: dto.name,
      start: this.shortAddress(pickup?.address),
      end: this.shortAddress(dropoff?.address),
      distanceKm: dto.totalDistanceKm,
      stops: dto.points.filter((p) => p.type === 'STOP').map((p) => this.shortAddress(p.address)),
    };
  }

  fetch() {
    this.loading.set(true);

    this.api.getFavoriteRoutes().subscribe({
      next: (res) => {
        const mapped = res.data?.map((dto) => this.mapDto(dto)) || [];
        this.routes.set(mapped);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
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
