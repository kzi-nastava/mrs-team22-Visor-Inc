import { Api } from '../api';
import { ApiClient } from '../api-client';
import { DriverSummaryDto, RideRequestDto, RideRequestResponseDto } from './home.model';
import { RequestConfig } from '../rest.model';
import { ApiResponse } from '../rest.model';

export class RideApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  getActiveDrivers() {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
      },
    };

    return this.apiClient.get<void, DriverSummaryDto[]>(
      '/api/drivers/active',
      config
    );
  }

  createRideRequest(body: RideRequestDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json',
      },
    };

    return this.apiClient.post<RideRequestDto, RideRequestResponseDto>(
      '/api/rides/requests',
      body,
      config
    );
  }
}
