import {Inject, Injectable} from '@angular/core';
import {ApiClient} from './api-client';
import {AuthenticationApi} from './authentication/authentication.api';
import {VoomApiService} from './voom-api-service';

@Injectable({
  providedIn: 'root',
})
class ApiService {

  public readonly authenticationApi;

  constructor(@Inject(VoomApiService) private apiClient: ApiClient) {
    this.authenticationApi = new AuthenticationApi(this.apiClient);
  }
}

export default ApiService
