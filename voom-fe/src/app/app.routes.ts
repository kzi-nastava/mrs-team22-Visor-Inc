import {Routes} from '@angular/router';
import {Login, ROUTE_LOGIN} from './unauthenticated/login/login';
import {ROUTE_USER_PROFILE, UserProfile} from './authenticated/user/user-profile/user-profile';
import {Registration, ROUTE_REGISTRATION} from './unauthenticated/registration/registration';
import {Home, ROUTE_HOME} from './unauthenticated/home/home';
import {DriverRideHistory, ROUTE_DRIVER_RIDE_HISTORY,} from './drivers/ride-history/driver-ride-history';
import {ForgotPassword, ROUTE_FORGOT_PASSWORD} from './unauthenticated/login/forgot-password/forgot-password';
import {ResetPassword, ROUTE_RESET_PASSWORD} from './unauthenticated/login/reset-password/reset-password';
import {DriverHome, ROUTE_DRIVER_HOME} from './drivers/driver-home/driver-home';
import { AdminRegisterDriver, ROUTE_ADMIN_REGISTER_DRIVER } from './authenticated/admin/register-driver/register-driver';
import { ActivateProfile, ROUTE_ACTIVATE_PROFILE } from './unauthenticated/activate/activate-profile/activate-profile';
import { ROUTE_USER_HOME, UserHome } from './authenticated/user/home/home';

export const routes: Routes = [
  {
    path: ROUTE_LOGIN,
    component: Login,
  },
  {
    path: ROUTE_REGISTRATION,
    component: Registration,
  },
  {
    path: ROUTE_FORGOT_PASSWORD,
    component: ForgotPassword,
  },
  {
    path: ROUTE_RESET_PASSWORD,
    component: ResetPassword,
  },
  {
    path: ROUTE_USER_PROFILE,
    component: UserProfile,
  },
  {
    path: ROUTE_DRIVER_HOME,
    component: DriverHome,
  },
  {
    path: ROUTE_HOME,
    component: Home,
  },
  {
    path: ROUTE_DRIVER_RIDE_HISTORY,
    component: DriverRideHistory,
  },
  {
    path: ROUTE_ADMIN_REGISTER_DRIVER,
    component: AdminRegisterDriver,
  },
  {
    path: ROUTE_ACTIVATE_PROFILE,
    component: ActivateProfile,
  },
  {
    path: ROUTE_USER_HOME,
    component: UserHome,
  },
  {
    path: '**',
    redirectTo: ROUTE_HOME,
  },
];
