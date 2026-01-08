import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type UserProfileResponseDto = {
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
};

export type UpdateUserProfileRequestDto = {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
};

export type UpdateUserPasswordRequestDto = {
  newPassword: string;
  confirmPassword: string;
};

export type DriverVehicleResponseDto = {
  model: string;
  vehicleType: 'STANDARD' | 'LUXURY' | 'VAN';
  licensePlate: string;
  numberOfSeats: number;
  babySeat: boolean;
  petFriendly: boolean;
  activeHoursLast24h?: number;
};

export type UpdateDriverVehicleRequestDto = {
  model: string;
  vehicleType: 'STANDARD' | 'LUXURY' | 'VAN';
  licensePlate: string;
  numberOfSeats: number;
  babySeat: boolean;
  petFriendly: boolean;
};

export type RoutePointType = 'PICKUP' | 'STOP' | 'DROPOFF';

export type RideRoutePointDto = {
  lat: number;
  lng: number;
  order: number;
  type: RoutePointType;
  address: string;
};

export type RideRequestDto = {
  route: {
    points: RideRoutePointDto[];
  };
  schedule: {
    type: 'NOW' | 'LATER';
    startAt: string; 
  };
  vehicleTypeId: number;
  preferences: {
    baby: boolean;
    pets: boolean;
  };
  linkedPassengers: string[];
};

export type DriverSummaryDto = {
  id: number;
  firstName: string;
  lastName: string;
};

export type RideRequestResponseDto = {
  requestId: number;
  status: 'ACCEPTED' | 'REJECTED' | 'PENDING';
  distanceKm: number;
  price: number;
  scheduledTime: string | null;
  driver: DriverSummaryDto | null;
};

@Injectable({ providedIn: 'root' })
export class RideApi {
  private readonly baseUrl = 'http://localhost:8080/api/rides';

  constructor(private http: HttpClient) {}

  createRideRequest(
    payload: RideRequestDto
  ): Observable<RideRequestResponseDto> {
    return this.http.post<RideRequestResponseDto>(
      `${this.baseUrl}/requests`,
      payload
    );
  }
}
