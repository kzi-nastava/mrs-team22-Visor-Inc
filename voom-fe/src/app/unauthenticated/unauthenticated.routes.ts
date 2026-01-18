import {Route} from '@angular/router';
import {Home, ROUTE_HOME} from './home/home';
import {ResetPassword, ROUTE_RESET_PASSWORD} from './login/reset-password/reset-password';
import {ForgotPassword, ROUTE_FORGOT_PASSWORD} from './login/forgot-password/forgot-password';
import {Registration, ROUTE_REGISTRATION} from './registration/registration';
import {Login, ROUTE_LOGIN} from './login/login';
import {ROUTE_VERIFY_PROFILE, VerifyProfile} from './activate/verify-profile/verify-profile';

export default [
  {
    path: ROUTE_HOME,
    component: Home,
  },
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
    path: ROUTE_VERIFY_PROFILE,
    component: VerifyProfile,
  },
  {
    path: '**',
    redirectTo: ROUTE_HOME,
  }
] satisfies Route[];
