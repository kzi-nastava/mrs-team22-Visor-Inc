import {CanActivateFn, Router, Routes} from '@angular/router';
import {AuthenticationService} from './shared/service/authentication-service';
import {inject} from '@angular/core';
import {map} from 'rxjs';
import {ROUTE_UNAUTHENTICATED_MAIN, UnauthenticatedMain} from './unauthenticated/unauthenticated-main';
import {MainShell} from './main-shell/main-shell';
import {ROUTE_USER_PAGES} from './main-shell/user-pages/user-pages';
import {ROUTE_ADMIN_PAGES} from './main-shell/admin-pages/admin-pages';
import {ROUTE_DRIVER_PAGES} from './main-shell/driver-pages/driver-pages';

export const unauthenticatedGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authenticationService = inject(AuthenticationService);
  return authenticationService.isAuthenticated().pipe(
    map((authenticated) => {
      return authenticated ? router.createUrlTree([""]) : true;
    })
  )
};

export const authenticatedGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authenticationService = inject(AuthenticationService);
  return authenticationService.isAuthenticated().pipe(
    map((authenticated) => {
        console.log("AuthenticatedGuard:", authenticated);
        return authenticated ? true : router.createUrlTree([ROUTE_UNAUTHENTICATED_MAIN]);
      })
    )
};

export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authenticationService = inject(AuthenticationService);

  return authenticationService.activeUser$.pipe(
    map((user) => {

      console.log("RoleGuard:", user, " required role:", route.data['role']);

      if (!user) {
        return router.createUrlTree([ROUTE_UNAUTHENTICATED_MAIN]);
      }

      return user.role === route.data['role'] ? true : router.createUrlTree([user.role.toLowerCase()]);
    }),
  );
}

export const routes: Routes = [
  {
    path: ROUTE_UNAUTHENTICATED_MAIN,
    component: UnauthenticatedMain,
    loadChildren: () => import('./unauthenticated/unauthenticated.routes'),
    canActivate: [unauthenticatedGuard],
  },
  {
    path: "",
    component: MainShell,
    canActivate: [authenticatedGuard],
    children: [
      {
        path: ROUTE_USER_PAGES,
        canActivate: [roleGuard],
        data: { 'role' : 'USER' },
        loadChildren: () => import('./main-shell/user-pages/user-pages.routes')
      },
      {
        path: ROUTE_DRIVER_PAGES,
        canActivate: [roleGuard],
        data: { 'role' : 'DRIVER' },
        loadChildren: () => import('./main-shell/driver-pages/driver-pages.routes')
      },
      {
        path: ROUTE_ADMIN_PAGES,
        canActivate: [roleGuard],
        data: { 'role' : 'ADMIN' },
        loadChildren: () => import('./main-shell/admin-pages/admin-pages.routes')
      },
      {
        path: "**",
        redirectTo: ROUTE_USER_PAGES,
      }
    ]
  },
  {
    path: '**',
    redirectTo: "",
  },
];
