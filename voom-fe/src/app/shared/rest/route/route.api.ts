import {Api} from '../api';
import {ApiClient} from '../api-client';
import {RequestConfig} from '../rest.model';
import {RouteEstimateRequestDto, RouteEstimateResponseDto} from './route.model';

export class RouteApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  getRouteEstimate(body: RouteEstimateRequestDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json',
      },
      authenticated: true,
    };

    return this.apiClient.post<RouteEstimateRequestDto, RouteEstimateResponseDto>(`/api/routes`, body, config);
  }

}
