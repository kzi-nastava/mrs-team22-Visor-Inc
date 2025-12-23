import { Component, inject } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { APP_ROUTES } from '../../../app.routes.constants';

@Component({
  selector: 'app-header',
  imports: [MatToolbarModule, MatButtonModule, MatIconModule, MatSidenavModule, RouterModule],
  templateUrl: './header-kt1.html',
  styleUrl: './header-kt1.css',
})
export class Header {
  //   /
  // /driver-home
  // /driver/rideHistory
  // /login
  // registration
  private router = inject(Router);
  menuOpen = false;
  readonly ROUTES = APP_ROUTES;

  home() {
    this.router.navigate([APP_ROUTES.HOME]);
  }

  driverHome() {
    this.router.navigate([APP_ROUTES.DRIVER.HOME]);
  }

  rideHistory() {
    this.router.navigate([APP_ROUTES.DRIVER.RIDE_HISTORY]);
  }

  login() {
    this.router.navigate([APP_ROUTES.AUTH.LOGIN]);
  }

  regisration() {
    this.router.navigate([APP_ROUTES.AUTH.REGISTRATION]);
  }

  userProfile() {
    this.router.navigate([APP_ROUTES.USER.PROFILE]);
  }
}
