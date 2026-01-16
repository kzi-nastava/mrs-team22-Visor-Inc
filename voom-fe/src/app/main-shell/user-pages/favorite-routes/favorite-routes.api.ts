import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type FavoriteRouteDto = {
  id: number;
  name: string;
  totalDistanceKm: number;
  points: {
    orderIndex: number;
    address: string;
    lat: number;
    lng: number;
    type: 'PICKUP' | 'STOP' | 'DROPOFF';
  }[];
};

@Injectable({ providedIn: 'root' })
export class FavoriteRoutesApi {
  private readonly baseUrl = 'http://localhost:8080/api/rides';

  constructor(private http: HttpClient) {}

  getFavoriteRoutes() {
    return this.http.get<FavoriteRouteDto[]>(`${this.baseUrl}/favorites`);
  }

  deleteFavoriteRoute(routeId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/favorites/${routeId}`);
  }
}
