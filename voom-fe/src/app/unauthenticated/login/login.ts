import { Component, inject } from '@angular/core';
import { ValueInputString } from '../../shared/value-input/value-input-string/value-input-string';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { Router } from '@angular/router';
import { ROUTE_REGISTRATION } from '../registration/registration';
import { ROUTE_FORGOT_PASSWORD } from './forgot-password/forgot-password';
import {ROUTE_HOME} from '../home/home';

export const ROUTE_LOGIN = 'login';

@Component({
  selector: 'app-login',
  imports: [ValueInputString, MatButton, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private router = inject(Router);

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

  forgotPassword() {
    this.router.navigate([ROUTE_FORGOT_PASSWORD]);
  }

  login() {
    this.router.navigate([ROUTE_HOME]);
  }

  registration() {
    this.router.navigate([ROUTE_REGISTRATION]);
  }
}
