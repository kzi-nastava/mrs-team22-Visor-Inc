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
  driverId?: number;
};

export type UpdateDriverVehicleRequestDto = {
  model: string;
  vehicleType: 'STANDARD' | 'LUXURY' | 'VAN';
  licensePlate: string;
  numberOfSeats: number;
  babySeat: boolean;
  petFriendly: boolean;
};

@Injectable({ providedIn: 'root' })
export class UserProfileApi {
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserProfileResponseDto> {
    return this.http.get<UserProfileResponseDto>(`${this.baseUrl}/users/me`);
  }

  updateProfile(body: UpdateUserProfileRequestDto): Observable<UserProfileResponseDto> {
    return this.http.put<UserProfileResponseDto>(`${this.baseUrl}/users/me`, body);
  }

  getMyVehicle(): Observable<DriverVehicleResponseDto> {
    return this.http.get<DriverVehicleResponseDto>(`${this.baseUrl}/drivers/me`);
  }

  updateMyVehicle(body: UpdateDriverVehicleRequestDto): Observable<DriverVehicleResponseDto> {
    return this.http.put<DriverVehicleResponseDto>(`${this.baseUrl}/drivers/me`, body);
  }

  updatePassword(body: UpdateUserPasswordRequestDto): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/users/me/password`, body);
  }
}
