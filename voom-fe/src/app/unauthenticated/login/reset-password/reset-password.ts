import {Component} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {ValueInputString} from "../../../shared/value-input/value-input-string/value-input-string";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {ROUTE_HOME} from '../../home/home';
import ApiService from '../../../shared/rest/api-service';
import {map} from 'rxjs';
import {lookUpQueryParam} from '../../../shared/util/url-util';
import {ROUTE_UNAUTHENTICATED_MAIN} from '../../unauthenticated-main';

export const ROUTE_RESET_PASSWORD = 'resetPassword';

@Component({
  selector: 'app-reset-password',
  imports: [
    MatButton,
    ValueInputString,
    ReactiveFormsModule
  ],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword {


  constructor(protected router: Router, protected apiService: ApiService) {
  }

  form = new FormGroup({
    password1: new FormControl<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(50)]),
    password2: new FormControl<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(50)]),
  });

  submit() {
    const password = this.form.value.password1;
    const confirmPassword = this.form.value.password2;
    const token = lookUpQueryParam("token");

    if (!password || !token || !confirmPassword || password !== confirmPassword) {
      return;
    }

    this.apiService.authenticationApi.resetPassword({
      token: token,
      password: password,
      confirmPassword: confirmPassword,
    }).pipe(
      map(result => result.data),
    ).subscribe(() => {
      console.log('Password reset successful');
      this.router.navigate([ROUTE_UNAUTHENTICATED_MAIN, ROUTE_HOME]);
    });
  }
}
