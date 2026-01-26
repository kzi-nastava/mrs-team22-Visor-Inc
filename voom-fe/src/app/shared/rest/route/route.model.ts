import {RoutePoint} from '../../../main-shell/user-pages/home/home';

export interface RouteEstimateRequestDto {
  routePoints: RoutePoint[];
}

export interface RouteEstimateResponseDto {
  duration: number,
  distance: number,
}
