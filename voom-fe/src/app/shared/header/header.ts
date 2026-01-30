import { Component, inject } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterModule } from '@angular/router';
import { AuthenticationService } from '../service/authentication-service';
import { toSignal } from '@angular/core/rxjs-interop';
import { ROUTE_REGISTRATION } from '../../unauthenticated/registration/registration';
import { ROUTE_LOGIN } from '../../unauthenticated/login/login';
import { ROUTE_USER_PROFILE } from '../../main-shell/user-pages/user-profile/user-profile';
import { ROUTE_DRIVER_RIDE_HISTORY } from '../../main-shell/driver-pages/ride-history/driver-ride-history';
import { ROUTE_FAVORITE_ROUTES } from '../../main-shell/user-pages/favorite-routes/favorite-routes';
import { ROUTE_UNAUTHENTICATED_MAIN } from '../../unauthenticated/unauthenticated-main';
import { ROUTE_USER_PAGES } from '../../main-shell/user-pages/user-pages';
import { ROUTE_DRIVER_PAGES } from '../../main-shell/driver-pages/driver-pages';
import { ROUTE_SCHEDULED_RIDES } from '../../main-shell/user-pages/scheduled-rides/scheduled-rides';
import {ROUTE_USER_ACTIVITY} from '../../main-shell/user-pages/user-activity/user-activity';

@Component({
  selector: 'app-header',
  imports: [MatToolbarModule, MatButtonModule, MatIconModule, RouterModule],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  private authenticationService = inject(AuthenticationService);

  isAuthenticated = toSignal(this.authenticationService.isAuthenticated());
  user = toSignal(this.authenticationService.activeUser$);

  constructor(private router: Router) {}

  protected favoriteRoutes() {
    this.router.navigate([ROUTE_USER_PAGES, ROUTE_FAVORITE_ROUTES]);
  }

  protected rideHistory() {
    const user = this.user();
    if (user?.role === 'DRIVER') {
      this.router.navigate([ROUTE_DRIVER_PAGES, ROUTE_DRIVER_RIDE_HISTORY]);
    } else {
      this.router.navigate([ROUTE_USER_PAGES, ROUTE_USER_ACTIVITY]);
    }
  }

  protected signOut() {
    this.authenticationService.logout();
    this.router.navigate([ROUTE_UNAUTHENTICATED_MAIN]);
  }

  protected profile() {
    const user = this.user();
    if (user?.role === 'DRIVER') {
      this.router.navigate([ROUTE_DRIVER_PAGES, ROUTE_USER_PROFILE]);
    } else {
      this.router.navigate([ROUTE_USER_PAGES, ROUTE_USER_PROFILE]);
    }
  }

  protected login() {
    this.router.navigate([ROUTE_UNAUTHENTICATED_MAIN, ROUTE_LOGIN]);
  }

  protected register() {
    this.router.navigate([ROUTE_UNAUTHENTICATED_MAIN, ROUTE_REGISTRATION]);
  }

  protected goHome() {
    const user = this.user();

    user
      ? this.router.navigate([user.role.toLowerCase()])
      : this.router.navigate([ROUTE_UNAUTHENTICATED_MAIN]);
  }

  protected scheduledRides() {
    this.router.navigate([ROUTE_USER_PAGES, ROUTE_SCHEDULED_RIDES]);
  }

  protected scheduledRidesDriver() {
    this.router.navigate([ROUTE_DRIVER_PAGES, ROUTE_SCHEDULED_RIDES]);
  }

}
