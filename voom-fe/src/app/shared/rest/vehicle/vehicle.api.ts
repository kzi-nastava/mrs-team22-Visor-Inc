import {Api} from '../api';
import {ApiClient} from '../api-client';
import {RequestConfig} from '../rest.model';
import {CreateVehicleDto, VehicleDto} from './vehicle.model';

export default class VehicleApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  getVehicles() {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.get<void, VehicleDto[]>('/api/vehicles', config);
  }

  getVehicle(id: number) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.get<void, VehicleDto>(`/api/vehicles/${id}`, config);
  }

  createVehicle(vehicleDto: CreateVehicleDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.post<CreateVehicleDto, VehicleDto>('/api/vehicles', vehicleDto, config);
  }

  updateVehicle(id: number, vehicleDto: VehicleDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.put<VehicleDto, VehicleDto>(`/api/vehicles/${id}`, vehicleDto, config);
  }

  deleteVehicle(id: number) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.delete<void, void>(`/api/vehicles/${id}`, config);
  }


}
