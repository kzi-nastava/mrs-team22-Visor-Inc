import {Injectable} from '@angular/core';
import {ApiClient} from './api-client';
import {AuthenticationApi} from './authentication/authentication.api';
import {RideApi} from './home/home.api';
import {DriverApi} from './driver/driver.api';
import {UserProfileApi} from '../../main-shell/user-pages/user-profile/user-profile.api';
import {UserApi} from './user/user.api';
import {UserRoleApi} from './user/user-role.api';
import VehicleApi from './vehicle/vehicle.api';
import VehicleTypeApi from './vehicle/vehicle-type.api';
import {DriverActivityApi} from './driver/driver-activity.api';
// import { RidesApi } from './ride/ride.api';

@Injectable({
  providedIn: 'root',
})
export default class ApiService {

  public readonly userApi;
  public readonly driverApi;
  public readonly authenticationApi;
  public readonly rideApi;
  public readonly profileApi;
  public readonly userRoleApi;
  public readonly vehicleApi;
  public readonly vehicleTypeApi;
  public readonly driverActivityApi;
  // public readonly ridesApi: RidesApi;

  constructor(private apiClient: ApiClient) {
    this.authenticationApi = new AuthenticationApi(this.apiClient);
    this.rideApi = new RideApi(this.apiClient);
    this.driverApi = new DriverApi(this.apiClient);
    this.profileApi = new UserProfileApi(this.apiClient);
    this.userApi = new UserApi(this.apiClient);
    this.userRoleApi = new UserRoleApi(this.apiClient);
    this.vehicleApi = new VehicleApi(this.apiClient);
    this.vehicleTypeApi = new VehicleTypeApi(this.apiClient);
    this.driverActivityApi = new DriverActivityApi(this.apiClient);
    // this.ridesApi = new RidesApi(this.apiClient);
  }
}
