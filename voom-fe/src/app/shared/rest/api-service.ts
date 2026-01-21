import {Injectable} from '@angular/core';
import {ApiClient} from './api-client';
import {AuthenticationApi} from './authentication/authentication.api';
import {RideApi} from './home/home.api';
import {DriverApi} from './driver/driver.api';
import {UserProfileApi} from '../../main-shell/user-pages/user-profile/user-profile.api';
import {UserApi} from './user/user.api';

@Injectable({
  providedIn: 'root',
})
export default class ApiService {
  public readonly userApi;
  public readonly driverApi;
  public readonly authenticationApi;
  public readonly rideApi;
  public readonly profileApi;

  constructor(private apiClient: ApiClient) {
    this.authenticationApi = new AuthenticationApi(this.apiClient);
    this.rideApi = new RideApi(this.apiClient);
    this.driverApi = new DriverApi(this.apiClient);
    this.profileApi = new UserProfileApi(this.apiClient);
    this.userApi = new UserApi(this.apiClient);
  }
}
