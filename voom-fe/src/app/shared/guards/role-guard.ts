import {CanActivateFn, Router} from '@angular/router';
import {AuthenticationService} from '../service/authentication-service';
import {inject} from '@angular/core';

export const roleGuard: CanActivateFn = (route, state) => {
  const authenticationService = inject(AuthenticationService);
  const router = inject(Router);

  console.log("RoleGuard:", route.data['roles']);

  const allowedRoles = route.data['roles'];
  const user = authenticationService.activeUser$.value;

  if (!authenticationService.isAuthenticated() || !user || !allowedRoles.includes(user.role)) {
    router.navigate(['']);
    return false;
  }

  return true;
};
