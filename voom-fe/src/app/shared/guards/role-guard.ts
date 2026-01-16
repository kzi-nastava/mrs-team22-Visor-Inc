import {CanActivateFn, Router} from '@angular/router';
import {AuthenticationService} from '../service/authentication-service';
import {inject} from '@angular/core';

export const roleGuard: CanActivateFn = (route, state) => {
  const authenticationService = inject(AuthenticationService);
  const router = inject(Router);

  const allowedRoles = route.data['role'];
  const user = authenticationService.accessToken;

  //TODO fix to work

  if (!authenticationService.isAuthenticated()) {
    router.navigate(['']);
    return false;
  }

  return true;
};
