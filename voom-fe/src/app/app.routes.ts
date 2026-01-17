import {CanActivateFn, Router, Routes} from '@angular/router';
import {AuthenticationService} from './shared/service/authentication-service';
import {inject} from '@angular/core';
import {map} from 'rxjs';
import {ROUTE_UNAUTHENTICATED_MAIN, UnauthenticatedMain} from './unauthenticated/unauthenticated-main';
import {MainShell, ROUTE_MAIN_SHELL} from './main-shell/main-shell';

export const unauthenticatedGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authenticationService = inject(AuthenticationService);
  const user = authenticationService.activeUser$.value;
  return authenticationService.isAuthenticated().pipe(
    map((authenticated) => {
      console.log("UnauthenticatedGuard:", authenticated);
      return authenticated ? router.createUrlTree([ROUTE_MAIN_SHELL, user!.role.toLowerCase()]) : true;
    })
  )
};

export const authenticatedGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authenticationService = inject(AuthenticationService);
  const user = authenticationService.activeUser$.value;
  return authenticationService.isAuthenticated().pipe(
  map((authenticated) => {
      console.log("AuthenticatedGuard:", authenticated);
      return authenticated ? true : router.createUrlTree([""]);
    })
  )
};

export const routes: Routes = [
  {
    path: ROUTE_MAIN_SHELL,
    component: MainShell,
    canActivate: [authenticatedGuard],
    loadChildren: () => import('./main-shell/main-shell.routes')
  },
  {
    path: ROUTE_UNAUTHENTICATED_MAIN,
    component: UnauthenticatedMain,
    loadChildren: () => import('./unauthenticated/unauthenticated.routes'),
    canActivate: [unauthenticatedGuard],
  },
  {
    path: '**',
    redirectTo: ROUTE_MAIN_SHELL,
  },
];
