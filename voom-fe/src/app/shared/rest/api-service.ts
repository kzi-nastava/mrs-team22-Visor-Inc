import {Inject, Injectable} from '@angular/core';
import {ApiClient} from './api-client';
import {AuthenticationApi} from './authentication/authentication.api';
import {VoomApiService} from './voom-api-service';
import {RideApi} from './home/home.api';

@Injectable({
  providedIn: 'root',
})
class ApiService {
  public readonly authenticationApi;
  public readonly rideApi;

  constructor(@Inject(VoomApiService) private apiClient: ApiClient) {
    this.authenticationApi = new AuthenticationApi(this.apiClient);
    this.rideApi = new RideApi(this.apiClient);
  }
}


export default ApiService
