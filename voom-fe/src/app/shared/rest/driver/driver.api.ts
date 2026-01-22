import {DriverDto} from './driver.model';
import {Api} from '../api';
import {ApiClient} from '../api-client';
import {RequestConfig} from '../rest.model';

export class DriverApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  getDrivers() {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.get<void, DriverDto[]>('/api/drivers', config);
  }

  getDriver(driverId: number) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.get<void, DriverDto>(`/api/drivers/${driverId}`, config);
  }

  updateDriver(driverId: number, driverDto: DriverDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.put<DriverDto, DriverDto>(`/api/drivers/${driverId}`, driverDto, config);
  }

  deleteDriver(driverId: number) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.delete<void, void>(`/api/drivers/${driverId}`, config);
  }
}
