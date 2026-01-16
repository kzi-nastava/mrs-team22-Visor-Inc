import {Component} from '@angular/core';
import {ValueInputString} from '../../shared/value-input/value-input-string/value-input-string';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {Router} from '@angular/router';
import {ROUTE_REGISTRATION} from '../registration/registration';
import {ROUTE_FORGOT_PASSWORD} from './forgot-password/forgot-password';
import {ROUTE_HOME} from '../home/home';
import ApiService from '../../shared/rest/api-service';
import {map} from 'rxjs';
import {AuthenticationService} from '../../shared/service/authentication-service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

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

  constructor(private router: Router, private apiService: ApiService, private authenticationService: AuthenticationService) {
    authenticationService.activeUser$.pipe(
      takeUntilDestroyed()
    ).subscribe(() => {
      this.router.navigate([ROUTE_HOME]);
    })
  }

  forgotPassword() {
    this.router.navigate([ROUTE_FORGOT_PASSWORD]);
  }

  login() {
    this.apiService.authenticationApi.login({
      email: this.form.value.email as string,
      password: this.form.value.password as string,
    }).pipe(
      map(response => response.data),
    ).subscribe((signInResponse) => {
      if (!signInResponse) {
        return;
      }

      this.authenticationService.setAuthentication(signInResponse);
    });
  }

  registration() {
    this.router.navigate([ROUTE_REGISTRATION]);
  }
}
