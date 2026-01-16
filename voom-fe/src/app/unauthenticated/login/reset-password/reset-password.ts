import {Component, inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {ValueInputString} from "../../../shared/value-input/value-input-string/value-input-string";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {ROUTE_HOME} from '../../home/home';
import {ApiService} from '../../../core/rest/api-service';
import {map} from 'rxjs';
import {lookUpQueryParam} from '../../../core/util/url-util';

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
    password1: new FormControl<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(255)]),
    password2: new FormControl<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(255)]),
  });

  submit() {
    const password = this.form.value.password1;
    const token = lookUpQueryParam('token');

    if (!password || !token) {
      return;
    }

    this.apiService.authenticationApi.resetPassword({
      token: token,
      password: password,
    }).pipe(
      map(result => result.data),
    ).subscribe(() => {
      this.router.navigate([ROUTE_HOME]);
    });
  }
}
