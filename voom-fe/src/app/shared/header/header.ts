import {Component, inject} from '@angular/core';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {Router, RouterModule} from '@angular/router';
import {AuthenticationService} from '../service/authentication-service';
import {toSignal} from '@angular/core/rxjs-interop';
import {ROUTE_REGISTRATION} from '../../unauthenticated/registration/registration';
import {ROUTE_LOGIN} from '../../unauthenticated/login/login';
import {ROUTE_USER_PROFILE} from '../../main-shell/user-pages/user-profile/user-profile';
import {ROUTE_DRIVER_RIDE_HISTORY} from '../../main-shell/driver-pages/ride-history/driver-ride-history';
import {ROUTE_FAVORITE_ROUTES} from '../../main-shell/user-pages/favorite-routes/favorite-routes';
import {ROUTE_HOME} from '../../unauthenticated/home/home';

@Component({
  selector: 'app-header',
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    RouterModule,

  ],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {

  private authenticationService = inject(AuthenticationService);

  isAuthenticated = toSignal(this.authenticationService.isAuthenticated());
  user = toSignal(this.authenticationService.activeUser$);

  constructor(private router: Router) {
  }

  protected favoriteRoutes() {
    this.router.navigate([ROUTE_FAVORITE_ROUTES]);
  }

  protected rideHistory() {
    this.router.navigate([ROUTE_DRIVER_RIDE_HISTORY]);
  }

  protected signOut() {
    this.authenticationService.logout();
    this.router.navigate([ROUTE_HOME]);
  }

  protected profile() {
    this.router.navigate([ROUTE_USER_PROFILE]);
  }

  protected login() {
    this.router.navigate([ROUTE_LOGIN]);
  }

  protected register() {
    this.router.navigate([ROUTE_REGISTRATION]);
  }

  protected goHome() {
    this.router.navigate([ROUTE_HOME]);
  }
}
