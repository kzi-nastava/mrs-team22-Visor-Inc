import {FavoriteRouteDto} from './favorite-routes.api';

export interface FavoriteRoute {
  dto: FavoriteRouteDto;
  id: number;
  name: string;
  start: string;
  end: string;
  distanceKm: number;
  stops: string[];
}
