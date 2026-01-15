import { Component } from '@angular/core';
import { Header } from '../../../core/layout/header-kt1/header-kt1';
import { Footer } from '../../../core/layout/footer/footer';
import { FavoriteRouteAccordion } from './favorite-routes-accordition/favorite-routes-accordition';

export const ROUTE_FAVORITE_ROUTES = 'user/favorite-routes';

export interface FavoriteRoute {
  id: number;
  name: string;
  start: string;
  end: string;
  distanceKm: number;
  stops: string[];
}

@Component({
  selector: 'app-favorite-routes',
  imports: [Header, Footer, FavoriteRouteAccordion],
  templateUrl: './favorite-routes.html',
  styleUrl: './favorite-routes.css',
})
export class FavoriteRoutes {
  routes: FavoriteRoute[] = [
    {
      id: 1,
      name: 'Route 1',
      start: 'Address 1',
      end: 'Address 2',
      distanceKm: 3,
      stops: [],
    },
    {
      id: 2,
      name: 'Route 2',
      start: 'Address 1',
      end: 'Address 2',
      distanceKm: 5,
      stops: ['Address 1', 'Address 1', 'Address 1'],
    },
    {
      id: 3,
      name: 'Route 3',
      start: 'Address 2',
      end: 'Address 3',
      distanceKm: 10,
      stops: [],
    },
  ];

  expandedRouteId: number | null = null;

  toggle(routeId: number) {
    this.expandedRouteId = this.expandedRouteId === routeId ? null : routeId;
  }
}
