import { HttpInterceptorFn } from '@angular/common/http';
import {switchMap, take} from 'rxjs';
import {inject} from '@angular/core';
import {AuthenticationService} from '../../shared/service/authentication-service';

export const authenticationInterceptor: HttpInterceptorFn = (req, next) => {
  const needsAuthorization = req.headers.has('Authorization') && !req.headers.get('Authorization');

  if (!needsAuthorization) {
    return next(req);
  }

  return inject(AuthenticationService).accessToken.pipe(
    take(1),
    switchMap((token) => {
      const authenticatedRequest = req.clone({ headers: req.headers.set('Authorization', 'Bearer ' + token) });
      return next(authenticatedRequest);
    })
  );
};
