import {Api} from '../api';
import {ApiClient} from '../api-client';
import {DriverStateChangeDto} from './driver-activity.model';
import {RequestConfig} from '../rest.model';

export class DriverActivityApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  getDriverState(userId: number) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.get<void, DriverStateChangeDto>(`/api/activity/${userId}`, config);
  }

  public changeDriverState(dto: DriverStateChangeDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.post<DriverStateChangeDto, DriverStateChangeDto>(`/api/activity`, dto, config);
  }

}
