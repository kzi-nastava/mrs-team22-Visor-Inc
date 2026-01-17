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

    return this.apiClient.post<LoginDto, TokenDto>(`/api/auth/login`, body, config);
  }

  register(body: RegistrationDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
    }

    return this.apiClient.post<RegistrationDto, User>('/api/auth/register', body, config);
  }

  refreshToken(body: string) {
    const config: RequestConfig = {
      headers: {

        accept: 'application/json',
        contentType: 'application/json',
      }
    };

    return this.apiClient.post<string, TokenDto>(`/api/auth/refreshToken`, body, config);
  }

  verifyUser(token: string) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
    };

    return this.apiClient.post<string, void>(`/api/auth/verifyUser`, token, config);
  }


  forgotPassword(email: string) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
    };

    return this.apiClient.post<string, void>(`/api/auth/forgotPassword`, email, config);
  }

  resetPassword(body: ResetPasswordDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
    };

    return this.apiClient.post<ResetPasswordDto, string>(`/api/auth/resetPassword`, body, config);
  }

}
