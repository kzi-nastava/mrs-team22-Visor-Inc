import {Api} from '../api';
import {RequestConfig} from '../rest.model';
import {CreateUserDto, UserProfileDto} from './user.model';
import {ApiClient} from '../api-client';
import {AdminCreateDriverDto, DriverDto} from '../driver/driver.model';

export class UserApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  getUsers() {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.get<void, UserProfileDto[]>('/api/users', config);
  }

  getUser(id: number) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.get<void, UserProfileDto>(`/api/users/${id}`, config);
  }

  createUser(dto: CreateUserDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.put<CreateUserDto, UserProfileDto>(`/api/users`, dto, config);
  }

  updateUser(id: number, userDto: UserProfileDto) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json',
        contentType: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.put<UserProfileDto, UserProfileDto>(`/api/users/${id}`, userDto, config);
  }

  deleteUser(id: number) {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.delete<void, void>(`/api/users/${id}`, config);
  }
}
