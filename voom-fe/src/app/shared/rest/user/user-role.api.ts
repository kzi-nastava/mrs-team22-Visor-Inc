import {Api} from '../api';
import {ApiClient} from '../api-client';
import {RequestConfig} from '../rest.model';
import {UserProfileDto} from './user.model';
import {UserRoleDto} from './user-role.model';

export class UserRoleApi extends Api {

  constructor(apiClient: ApiClient) {
    super(apiClient);
  }

  getUserRoles() {
    const config: RequestConfig = {
      headers: {
        accept: 'application/json'
      },
      authenticated: true,
    };

    return this.apiClient.get<void, UserRoleDto[]>('/api/roles', config);
  }

}
