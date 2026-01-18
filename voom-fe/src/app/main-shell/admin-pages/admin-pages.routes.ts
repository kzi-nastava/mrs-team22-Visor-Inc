import {AdminRegisterDriver, ROUTE_ADMIN_REGISTER_DRIVER} from './register-driver/register-driver';
import {Route} from '@angular/router';
import {
  ActivateProfile,
  ROUTE_ACTIVATE_PROFILE
} from '../../unauthenticated/activate/activate-profile/activate-profile';
import {UserPages} from '../user-pages/user-pages';
import {AdminPages} from './admin-pages';

export default [
  {
    path: '',
    component: AdminPages,
    children: [
      {
        path: ROUTE_ADMIN_REGISTER_DRIVER,
        component: AdminRegisterDriver,
      },
      {
        path: ROUTE_ACTIVATE_PROFILE,
        component: ActivateProfile,
      },
      {
        path: '**',
        redirectTo: ROUTE_ADMIN_REGISTER_DRIVER,
      }
    ]
  },
  {
    path: '**',
    redirectTo: '',
  }
] satisfies Route[];
