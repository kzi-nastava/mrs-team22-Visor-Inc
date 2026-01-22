import {ApiClient} from './api-client';
import ApiService from './api-service';

export abstract class Api {
  protected apiClient: ApiClient;

  constructor(apiClient: ApiClient) {
    this.apiClient = apiClient;
  }

}
