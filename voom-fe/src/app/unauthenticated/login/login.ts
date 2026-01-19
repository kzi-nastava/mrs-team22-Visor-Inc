import {Component, inject} from '@angular/core';
import {ValueInputString} from '../../shared/value-input/value-input-string/value-input-string';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {Router} from '@angular/router';
import {ROUTE_REGISTRATION} from '../registration/registration';
import {ROUTE_FORGOT_PASSWORD} from './forgot-password/forgot-password';
import ApiService from '../../shared/rest/api-service';
import {map} from 'rxjs';
import {AuthenticationService} from '../../shared/service/authentication-service';
import {ROUTE_UNAUTHENTICATED_MAIN} from '../unauthenticated-main';

export const ROUTE_LOGIN = 'login';

@Component({
  selector: 'app-login',
  imports: [ValueInputString, MatButton, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  form = new FormGroup({
    email: new FormControl<string>('', [
      Validators.required,
      Validators.email,
      Validators.maxLength(255),
    ]),
    password: new FormControl<string>('', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(255),
    ]),
  });

  private apiService = inject(ApiService);

  constructor(private router: Router, private authenticationService: AuthenticationService) {
  }

  forgotPassword() {
    this.router.navigate([ROUTE_UNAUTHENTICATED_MAIN, ROUTE_FORGOT_PASSWORD]);
  }

  login() {
    this.apiService.authenticationApi.login({
      email: this.form.value.email as string,
      password: this.form.value.password as string,
    }).pipe(
      map(response => response.data),
    ).subscribe((signInResponse) => {

      console.log("Login successful:", signInResponse);

      if (!signInResponse) {
        return;
      }

      this.authenticationService.setAuthentication(signInResponse);
    });
  }

  registration() {
    this.router.navigate([ROUTE_UNAUTHENTICATED_MAIN, ROUTE_REGISTRATION]);
  }
}
