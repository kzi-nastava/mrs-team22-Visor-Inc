import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiClient } from '../../../shared/rest/api-client';
import { ApiResponse, RequestConfig } from '../../../shared/rest/rest.model';
import { VoomApiService } from '../../../shared/rest/voom-api-service';

export type DriverVehicleChangeRequestDto = {
  id: number;
  driverId: number;
  driverFullName: string;
  model: string;
  vehicleType: 'STANDARD' | 'LUXURY' | 'VAN';
  licensePlate: string;
  numberOfSeats: number;
  babySeat: boolean;
  petFriendly: boolean;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createdAt: string;
};

@Injectable({ providedIn: 'root' })
export class AdminVehicleRequestsApi {

  private readonly baseUrl = '/api/admin/vehicle-requests';

  constructor(@Inject(VoomApiService) private apiClient: ApiClient) {}

  getRequest(id: string): Observable<ApiResponse<DriverVehicleChangeRequestDto>> {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
      },
      authenticated: true,
    };

    return this.apiClient.get<void, DriverVehicleChangeRequestDto>(
      `${this.baseUrl}/${id}`,
      config
    );
  }

  approve(id: string): Observable<ApiResponse<void>> {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
      },
      authenticated: true,
    };

    return this.apiClient.post<void, void>(
      `${this.baseUrl}/${id}/approve`,
      undefined,
      config
    );
  }

  reject(id: string): Observable<ApiResponse<void>> {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
      },
      authenticated: true,
    };

    return this.apiClient.post<void, void>(
      `${this.baseUrl}/${id}/reject`,
      undefined,
      config
    );
  }
}
