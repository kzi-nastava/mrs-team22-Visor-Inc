import {Api} from '../api';
import {ApiClient} from '../api-client';
import {ForgotPasswordDto, ResetPasswordDto, SignInRequest, SignInResponse} from './authentication.model';
import {RequestConfig} from '../rest.model';


export class AuthenticationApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  login(body: SignInRequest) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
    }

    return this.apiClient.post<SignInRequest, SignInResponse>(`/auth/login`, body, config);
  }

  refreshToken(body: string) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json',
      }
    };

    return this.apiClient.put<string, SignInResponse>(`/auth/refreshToken`, body, config);
  }

  forgotPassword(body: ForgotPasswordDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
    };

    return this.apiClient.post<ForgotPasswordDto, void>(`/auth/forgotPassword`, body, config);
  }

  resetPassword(body: ResetPasswordDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
    };

    return this.apiClient.post<ResetPasswordDto, void>(`/auth/resetPassword`, body, config);
  }

}
