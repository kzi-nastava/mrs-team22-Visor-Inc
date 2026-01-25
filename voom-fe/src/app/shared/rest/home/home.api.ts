import { Api } from '../api';
import { ApiClient } from '../api-client';
import { DriverSummaryDto, OngoingRideDto, RatingRequestDto, RideHistoryDto, RideReportRequestDto, RideRequestDto, RideRequestResponseDto, RideResponseDto } from './home.model';
import { RequestConfig } from '../rest.model';
import { ApiResponse } from '../rest.model';
import { ActiveRideDto } from '../../../main-shell/user-pages/home/home.api';
import { Observable } from 'rxjs/internal/Observable';

export class RideApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  getRide(id: number) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
      },
      authenticated: true,
    };

    return this.apiClient.get<void, OngoingRideDto>(
      `/api/rides/ongoing`,
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
    authenticated: true,
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
    authenticated: true,
  };

  return this.apiClient.post<RatingRequestDto, void>(
    `/api/rating/${rideId}`,
    body,
    config
  );
}

finishOngoingRide() {
  const config: RequestConfig = {
    headers: {
      accept: 'application/json',
    },
    authenticated: true, 
  };

  return this.apiClient.post<void, OngoingRideDto>(
    '/api/rides/finish-ongoing',
    undefined,
    config
  );
}

getOngoingRide(): Observable<ApiResponse<ActiveRideDto>> {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
      authenticated: true,
    };
    return this.apiClient.get<void, ActiveRideDto>(`/api/rides/ongoing`, config);
  }

getDriverRideHistory(dateFrom?: Date | null, dateTo?: Date | null, sort: 'asc' | 'desc' = 'asc') {
  const queryParams: any = {
    sort: sort.toUpperCase()
  };

  if (dateFrom) {
    queryParams.dateFrom = dateFrom.toISOString();
  }
  if (dateTo) {
    queryParams.dateTo = dateTo.toISOString();
  }

  const config: RequestConfig = {
    headers: { 
      accept: 'application/json',
      contentType: 'application/json' 
    },
    params: queryParams,
    authenticated: true,
  };

  return this.apiClient.get<void, RideHistoryDto[]>(
    `/api/rides/driver/history`, 
    config
  );
}

}
