import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiClient } from '../../../shared/rest/api-client';
import { ApiResponse, RequestConfig } from '../../../shared/rest/rest.model';
import { VoomApiService } from '../../../shared/rest/voom-api-service';

export type RegisterDriverRequestDto = {
  firstName: string;
  lastName: string;
  birthDate: string;
  email: string;
  phoneNumber: string;
  address: string;
  vehicle: {
    model: string;
    vehicleType: 'STANDARD' | 'LUXURY' | 'VAN';
    licensePlate: string;
    numberOfSeats: number;
    babySeat: boolean;
    petFriendly: boolean;
  };
};

@Injectable({
  providedIn: 'root',
})
export class RegisterDriverApi {

  private readonly baseUrl = '/api/drivers';

  constructor(@Inject(VoomApiService) private apiClient: ApiClient) {}

  registerDriver(
    payload: RegisterDriverRequestDto
  ): Observable<ApiResponse<void>> {

    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json',
      },
      authenticated: true,
    };

    return this.apiClient.post<RegisterDriverRequestDto, void>(
      `${this.baseUrl}`,
      payload,
      config
    );
  }
}
