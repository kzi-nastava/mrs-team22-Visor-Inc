import {DriverDto} from './driver.model';
import {Api} from '../api';
import {ApiClient} from '../api-client';

export class DriverApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  getDrivers() {
    const config: any = {
      headers: {
        accept: 'application/json'
      }
    };

    return this.apiClient.get<void, DriverDto[]>('/api/drivers', config);
  }

  getDriver(driverId: number) {
    const config: any = {
      headers: {
        accept: 'application/json'
      }
    };

    return this.apiClient.get<void, DriverDto>(`/api/drivers/${driverId}`, config);
  }

  updateDriver(driverId: number, driverSummaryDto: DriverDto) {
    const config: any = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      }
    };

    return this.apiClient.put<DriverDto, DriverDto>(`/api/drivers/${driverId}`, driverSummaryDto, config);
  }

  deleteDriver(driverId: number) {
    const config: any = {
      headers: {
        accept: 'application/json'
      }
    };

    return this.apiClient.delete<void, void>(`/api/drivers/${driverId}`, config);
  }
}
