import { Routes } from '@angular/router';
import { Login, ROUTE_LOGIN } from './unauthenticated/login/login';
import { UserProfile, ROUTE_USER_PROFILE } from './authenticated/user/user-profile/user-profile';
import { Registration, ROUTE_REGISTRATION } from './unauthenticated/registration/registration';
import { Home, ROUTE_HOME } from './core/layout/home/home';
import {
  DriverRideHistory,
  ROUTE_DRIVER_RIDE_HISTORY,
} from './drivers/ride-history/driver-ride-history';

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
    path: ROUTE_USER_PROFILE,
    component: UserProfile,
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: ROUTE_HOME,
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
    path: '**',
    redirectTo: ROUTE_HOME,
  },
];
