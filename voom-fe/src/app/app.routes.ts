import { Routes } from '@angular/router';
import {Login, ROUTE_LOGIN} from './unauthenticated/login/login';
import {Registration, ROUTE_REGISTRATION} from './unauthenticated/registration/registration';

export const routes: Routes = [{
  path: ROUTE_LOGIN,
  component: Login,
}, {
  path: ROUTE_REGISTRATION,
  component: Registration,
}, {
  path: '**',
  redirectTo: ROUTE_LOGIN,
}];
