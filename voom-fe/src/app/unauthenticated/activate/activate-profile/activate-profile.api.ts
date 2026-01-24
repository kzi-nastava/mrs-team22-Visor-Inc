
import { Injectable } from '@angular/core';
import { Api } from '../../../shared/rest/api';
import { ApiClient } from '../../../shared/rest/api-client';
import { RequestConfig } from '../../../shared/rest/rest.model';


export type ActivateProfileRequestDto = {
  token: string;
  password: string;
  confirmPassword: string;
};

@Injectable({
  providedIn: 'root',
})
export class ActivateProfileApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  activateProfile(body: ActivateProfileRequestDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json',
      },
    };

    return this.apiClient.post<ActivateProfileRequestDto, void>(
      '/api/drivers/activation',
      body,
      config
    );
  }
}
