import {Component, inject} from '@angular/core';
import {MatButton} from '@angular/material/button';
import ApiService from '../../../shared/rest/api-service';
import {map} from 'rxjs';
import {Router} from '@angular/router';
import {ROUTE_LOGIN} from '../../login/login';
import {lookUpQueryParam} from '../../../shared/util/url-util';

export const ROUTE_VERIFY_PROFILE = 'verifyUser';

@Component({
  selector: 'app-verify-profile',
  imports: [
    MatButton
  ],
  templateUrl: './verify-profile.html',
  styleUrl: './verify-profile.css',
})
export class VerifyProfile {

  private apiService = inject(ApiService);

  constructor(private router: Router) {}

  protected verify() {
    const token = lookUpQueryParam('token');

    if (!token) {
      return;
    }

    this.apiService.authenticationApi.verifyUser(token).pipe(
      map(result => result.data)
    ).subscribe(() => {
      console.log('Profile verified successfully');
      this.router.navigate([ROUTE_LOGIN]);
    });
  }
}
