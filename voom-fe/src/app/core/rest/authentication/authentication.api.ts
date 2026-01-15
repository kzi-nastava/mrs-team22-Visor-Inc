import {Api} from '../api';
import {ApiClient} from '../api-client';
import {SignInRequest, SignInResponse} from './authentication.model';
import {RequestConfig} from '../rest.model';


export class AuthenticationApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  signIn(body: SignInRequest) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
    }

    return this.apiClient.post<SignInRequest, SignInResponse>(`/api/signIn`, body, config);
  }

  refreshToken(body: string) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json',
      }
    };

    return this.apiClient.put<string, SignInResponse>(`/api/refreshToken`, body, config);
  }

  resetPassword(email: string) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
    };

    return this.apiClient.put<string, void>(`/api/passwordReset`, email, config);
  }

}
