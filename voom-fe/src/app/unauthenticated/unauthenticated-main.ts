import { Component } from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {AuthenticationService} from '../shared/service/authentication-service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {ROUTE_MAIN_SHELL} from '../main-shell/main-shell';

export const ROUTE_UNAUTHENTICATED_MAIN = "";

@Component({
  selector: 'app-unauthenticated-main',
  imports: [
    RouterOutlet
  ],
  templateUrl: './unauthenticated-main.html',
  styleUrl: './unauthenticated-main.css',
})
export class UnauthenticatedMain {

  constructor(private authenticationService: AuthenticationService, private router: Router) {
    this.authenticationService.activeUser$.pipe(
      takeUntilDestroyed()
    ).subscribe((user) => {
      if (user) {
        this.router.navigate([ROUTE_MAIN_SHELL, user.role.toLowerCase()]);
      }
    });
  }

}
