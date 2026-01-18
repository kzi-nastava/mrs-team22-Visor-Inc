import { Api } from '../api';
import { ApiClient } from '../api-client';
import { DriverSummaryDto, RatingRequestDto, RideReportRequestDto, RideRequestDto, RideRequestResponseDto, RideResponseDto } from './home.model';
import { RequestConfig } from '../rest.model';
import { ApiResponse } from '../rest.model';

export class RideApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  getRide(id: number) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
      },
    };

    return this.apiClient.get<void, RideResponseDto>(
      `/api/rides/${id}`,
      config
    );
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

  reportRide(rideId: number, body: RideReportRequestDto) {
  const config: RequestConfig = {
    headers: {
      contentType: 'application/json',
    },
  };

  return this.apiClient.post<RideReportRequestDto, void>(
    `/api/rides/${rideId}/report`,
    body,
    config
  );
}

rateRide(rideId: number, body: RatingRequestDto) {
  const config: RequestConfig = {
    headers: {
      contentType: 'application/json',
    },
  };

  return this.apiClient.post<RatingRequestDto, void>(
    `/api/rides/${rideId}/rate`,
    body,
    config
  );
}


}
