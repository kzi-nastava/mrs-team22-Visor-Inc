import {CanActivateFn, Router, Routes} from '@angular/router';
import {Login, ROUTE_LOGIN} from './unauthenticated/login/login';
import {Registration, ROUTE_REGISTRATION} from './unauthenticated/registration/registration';
import {ForgotPassword, ROUTE_FORGOT_PASSWORD} from './unauthenticated/login/forgot-password/forgot-password';
import {ResetPassword, ROUTE_RESET_PASSWORD} from './unauthenticated/login/reset-password/reset-password';
import {AuthenticationService} from './shared/service/authentication-service';
import {inject} from '@angular/core';
import {map} from 'rxjs';
import {MainShell, ROUTE_MAIN_SHELL} from './main-shell/main-shell';
import {Home, ROUTE_HOME} from './unauthenticated/home/home';

export const unauthenticatedGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  return inject(AuthenticationService).isAuthenticated().pipe(
    map((authenticated) => {
      return authenticated ? router.createUrlTree(['']) : true;
    })
  )
};

export const authenticatedGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  return inject(AuthenticationService).isAuthenticated().pipe(
    map((authenticated) => {
      return authenticated ? true : router.createUrlTree(['']);
    })
  )
};

export const routes: Routes = [
  {
    path: ROUTE_HOME,
    component: Home,
    canActivate: [unauthenticatedGuard],
  },
  {
    path: ROUTE_LOGIN,
    component: Login,
    canActivate: [unauthenticatedGuard],
  },
  {
    path: ROUTE_REGISTRATION,
    component: Registration,
    canActivate: [unauthenticatedGuard],
  },
  {
    path: ROUTE_FORGOT_PASSWORD,
    component: ForgotPassword,
    canActivate: [unauthenticatedGuard],
  },
  {
    path: ROUTE_RESET_PASSWORD,
    component: ResetPassword,
    canActivate: [unauthenticatedGuard],
  },
  {
    path: ROUTE_MAIN_SHELL,
    component: MainShell,
    canActivate: [authenticatedGuard],
    loadChildren: () => import('./main-shell/main-shell.routes')
  },
  {
    path: '**',
    redirectTo: ROUTE_HOME,
  },
];
