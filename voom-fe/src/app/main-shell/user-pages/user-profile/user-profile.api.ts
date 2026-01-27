import { Inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { VoomApiService } from '../../../shared/rest/voom-api-service';
import { ApiClient } from '../../../shared/rest/api-client';
import { ApiResponse, RequestConfig } from '../../../shared/rest/rest.model';

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
  activeLast24Hours?: number;
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
  private readonly baseUrl = '/api/users';
  private readonly driversBaseUrl = '/api/drivers';

  constructor(@Inject(VoomApiService) private apiClient: ApiClient) {}

  getProfile(): Observable<ApiResponse<UserProfileResponseDto>> {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
      },
      authenticated: true,
    };

    return this.apiClient.get<void, UserProfileResponseDto>(
      `${this.baseUrl}/me`,
      config
    );
  }

  updateProfile(
    body: UpdateUserProfileRequestDto
  ): Observable<ApiResponse<UserProfileResponseDto>> {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json',
      },
      authenticated: true,
    };

    return this.apiClient.put<UpdateUserProfileRequestDto, UserProfileResponseDto>(
      `${this.baseUrl}/me`,
      body,
      config
    );
  }

  updatePassword(
    body: UpdateUserPasswordRequestDto
  ): Observable<ApiResponse<void>> {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json',
      },
      authenticated: true,
    };

    return this.apiClient.put<UpdateUserPasswordRequestDto, void>(
      `${this.baseUrl}/me/password`,
      body,
      config
    );
  }

  getMyVehicle(): Observable<ApiResponse<DriverVehicleResponseDto>> {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
      },
      authenticated: true,
    };

    return this.apiClient.get<void, DriverVehicleResponseDto>(
      `${this.driversBaseUrl}/me`,
      config
    );
  }

  updateMyVehicle(
    body: UpdateDriverVehicleRequestDto
  ): Observable<ApiResponse<DriverVehicleResponseDto>> {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json',
      },
      authenticated: true,
    };

    return this.apiClient.put<UpdateDriverVehicleRequestDto, DriverVehicleResponseDto>(
      `${this.driversBaseUrl}/me`,
      body,
      config
    );
  }
}

