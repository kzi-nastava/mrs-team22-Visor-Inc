import {AdminRegisterDriver, ROUTE_ADMIN_REGISTER_DRIVER} from './register-driver/register-driver';
import {Route} from '@angular/router';
import {
  ActivateProfile,
  ROUTE_ACTIVATE_PROFILE
} from '../../unauthenticated/activate/activate-profile/activate-profile';

export default [
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
] satisfies Route[];
