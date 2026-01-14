import {Injectable} from '@angular/core';
import {ApiClient} from './api-client';
import {AuthenticationApi} from './authentication/authentication.api';

@Injectable({
  providedIn: 'root',
})
export class ApiService {

  public readonly authenticationApi;

  constructor(private apiClient: ApiClient) {
    this.authenticationApi = new AuthenticationApi(this.apiClient);
  }
}
