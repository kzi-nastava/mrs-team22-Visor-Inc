import {ApiClient} from './api-client';

export abstract class Api {
  protected apiClient: ApiClient;

  protected constructor(apiClient: ApiClient) {
    this.apiClient = apiClient;
  }

}
