import {Component, inject} from '@angular/core';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {Router, RouterModule} from '@angular/router';
import {AuthenticationService} from '../service/authentication-service';
import {toSignal} from '@angular/core/rxjs-interop';
import {ROUTE_REGISTRATION} from '../../unauthenticated/registration/registration';
import {ROUTE_LOGIN} from '../../unauthenticated/login/login';

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

  constructor(private router: Router) {
  }

  protected favoriteRoutes() {

  }

  protected rideHistory() {

  }

  protected signOut() {

  }

  protected profile() {

  }

  protected login() {
    this.router.navigate([ROUTE_LOGIN]);
  }

  protected register() {
    this.router.navigate([ROUTE_REGISTRATION]);
  }

}
