import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { VoomApiService } from '../../../shared/rest/voom-api-service';
import { ApiClient } from '../../../shared/rest/api-client';
import { ApiResponse, RequestConfig } from '../../../shared/rest/rest.model';

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

  private readonly baseUrl = '/api/rides';

  constructor(@Inject(VoomApiService) private apiClient: ApiClient) {}

  getFavoriteRoutes(): Observable<ApiResponse<FavoriteRouteDto[]>> {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
      },
      authenticated: true,
    };

    return this.apiClient.get<void, FavoriteRouteDto[]>(
      `${this.baseUrl}/favorites`,
      config
    );
  }

  deleteFavoriteRoute(routeId: number): Observable<ApiResponse<void>> {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
      },
      authenticated: true,
    };

    return this.apiClient.delete<void, void>(
      `${this.baseUrl}/favorites/${routeId}`,
      config
    );
  }
}
