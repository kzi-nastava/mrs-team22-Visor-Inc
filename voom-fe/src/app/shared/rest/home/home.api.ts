import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import type {
  UserProfileResponseDto,
  UpdateUserProfileRequestDto,
  UpdateUserPasswordRequestDto,
  DriverVehicleResponseDto,
  UpdateDriverVehicleRequestDto,
  RoutePointType,
  RideRoutePointDto,
  ScheduledRideDto,
  DriverAssignedDto,
  RideRequestDto,
  DriverSummaryDto,
  RideRequestResponseDto
} from './home.model';
import { ApiClient } from '../api-client';



@Injectable({ providedIn: 'root' })
export class RideApi {
  private readonly baseUrl = 'http://localhost:8080/api/rides';
  private readonly driversBaseUrl = 'http://localhost:8080/api/drivers';

  constructor(private http: HttpClient) {}

  createRideRequest(payload: RideRequestDto): Observable<RideRequestResponseDto> {
    return this.http.post<RideRequestResponseDto>(`${this.baseUrl}/requests`, payload);
  }

  getActiveDrivers(): Observable<DriverSummaryDto[]> {
    return this.http.get<DriverSummaryDto[]>(`${this.driversBaseUrl}/active`);
  }

}
