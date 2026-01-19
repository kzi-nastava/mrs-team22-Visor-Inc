import {Inject, Injectable} from '@angular/core';
import {ApiClient} from './api-client';
import {AuthenticationApi} from './authentication/authentication.api';
import {VoomApiService} from './voom-api-service';
import {RideApi} from './home/home.api';
import { UserProfileApi } from '../../main-shell/user-pages/user-profile/user-profile.api';

@Injectable({
  providedIn: 'root',
})
class ApiService {
  public readonly authenticationApi;
  public readonly rideApi;
  public readonly profileApi;

  constructor(@Inject(VoomApiService) private apiClient: ApiClient) {
    this.authenticationApi = new AuthenticationApi(this.apiClient);
    this.rideApi = new RideApi(this.apiClient);
    this.profileApi = new UserProfileApi(this.apiClient);
  }
}


export default ApiService
