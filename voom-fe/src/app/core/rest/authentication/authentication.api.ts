import {Api} from '../api';
import {ApiClient} from '../api-client';
import {LoginDto, TokenDto, RegistrationDto, ResetPasswordDto, User} from './authentication.model';
import {RequestConfig} from '../rest.model';


export class AuthenticationApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  login(body: LoginDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
    }

    return this.apiClient.post<LoginDto, TokenDto>(`/api/login`, body, config);
  }

  register(body: RegistrationDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
    }

    return this.apiClient.post<RegistrationDto, User>('/api/register', body, config);
  }

  refreshToken(body: string) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json',
      }
    };

    return this.apiClient.put<string, string>(`/api/refreshToken`, body, config);
  }

  forgotPassword(email: string) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
    };

    return this.apiClient.put<string, void>(`/api/forgotPassword`, email, config);
  }

  resetPassword(body: ResetPasswordDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
    };

    return this.apiClient.put<ResetPasswordDto, string>(`/api/resetPassword`, body, config);
  }

}
