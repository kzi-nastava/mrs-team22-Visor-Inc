import {Component, inject} from '@angular/core';
import {Router, RouterOutlet} from '@angular/router';
import {AuthenticationService} from '../shared/service/authentication-service';
import {takeUntilDestroyed, toSignal} from '@angular/core/rxjs-interop';

export const ROUTE_UNAUTHENTICATED_MAIN = "u";

@Component({
  selector: 'app-unauthenticated-main',
  imports: [
    RouterOutlet
  ],
  templateUrl: './unauthenticated-main.html',
  styleUrl: './unauthenticated-main.css',
})
export class UnauthenticatedMain {

  private authenticationService = inject(AuthenticationService);

  user = toSignal(this.authenticationService.activeUser$);

  constructor(private router: Router) {
    this.authenticationService.activeUser$.pipe(
      takeUntilDestroyed()
    ).subscribe((user) => {
      if (user) {
        console.log("UnauthenticatedMain: navigating to main shell for user role", user.role);
        this.router.navigate(["", user.role.toLowerCase()]);
      }
    });
  }
}
