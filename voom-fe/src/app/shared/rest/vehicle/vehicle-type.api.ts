import {Api} from '../api';
import {ApiClient} from '../api-client';
import {RequestConfig} from '../rest.model';
import {CreateVehicleTypeDto, VehicleTypeDto} from './vehicle-type.model';

export default class VehicleTypeApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  getVehicleTypes() {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.get<void, VehicleTypeDto[]>('/api/vehicleTypes', config);
  }

  getVehicleType(id: number) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.get<void, VehicleTypeDto>(`/api/vehicleTypes/${id}`, config);
  }

  createVehicleType(vehicleTypeDto: CreateVehicleTypeDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.post<CreateVehicleTypeDto, VehicleTypeDto>('/api/vehicleTypes', vehicleTypeDto, config);
  }

  updateVehicleType(id: number, vehicleTypeDto: VehicleTypeDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.put<VehicleTypeDto, VehicleTypeDto>(`/api/vehicleTypes/${id}`, vehicleTypeDto, config);
  }

  deleteVehicleType(id: number) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.delete<void, void>(`/api/vehicleTypes/${id}`, config);
  }
}
