import {CanActivateFn, Router, Routes} from '@angular/router';
import {AuthenticationService} from './shared/service/authentication-service';
import {inject} from '@angular/core';
import {filter, map, of, switchMap, take} from 'rxjs';
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
      console.log('UnauthenticatedGuard', authenticated);
      return authenticated ? router.createUrlTree([""]) : true;
    })
  )
};


export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authenticationService = inject(AuthenticationService);

  return authenticationService.isAuthenticated().pipe(
    take(1),
    switchMap((authenticated) => {

      console.log("RoleGuard:", authenticated);

      if (!authenticated) {
        authenticationService.logout();
        return of(router.createUrlTree([ROUTE_UNAUTHENTICATED_MAIN]));
      }

      return authenticationService.isReady$.pipe(
        filter(ready => ready),
        take(1),
        switchMap(() => authenticationService.activeUser$.pipe(take(1))),
        map((user) => {

          console.log("RoleGuard:", user, " required role:", route.data['role']);
          console.log("Route", route)
          console.log("stateUrl", state.url);

          if (!user) {
            authenticationService.logout();
            return router.createUrlTree([ROUTE_UNAUTHENTICATED_MAIN]);
          }

          const stateUrl = state.url;
          const requiredRole = route.data['role'];
          const userRole = user.role.toLowerCase()
          const urlPath = stateUrl.includes(userRole.toLowerCase()) ? stateUrl : userRole;

          console.log("URL PATH", urlPath);

          return user.role === requiredRole ? true : router.createUrlTree([urlPath]);
        }),
      );
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
