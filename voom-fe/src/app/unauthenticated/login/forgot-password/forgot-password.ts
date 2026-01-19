import {Component, inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {ValueInputString} from "../../../shared/value-input/value-input-string/value-input-string";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {ROUTE_RESET_PASSWORD} from '../reset-password/reset-password';
import ApiService from '../../../shared/rest/api-service';
import {map} from 'rxjs';
import {ROUTE_HOME} from '../../home/home';
import {ROUTE_UNAUTHENTICATED_MAIN} from '../../unauthenticated-main';

export const ROUTE_FORGOT_PASSWORD = 'forgotPassword';

@Component({
  selector: 'app-forgot-password',
  imports: [
    MatButton,
    ValueInputString,
    ReactiveFormsModule
  ],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {

  constructor(protected router: Router, protected apiService: ApiService) {
  }

  form = new FormGroup({
    email: new FormControl<string>('', [Validators.required, Validators.email, Validators.maxLength(255)]),
  });

  submit() {
    const email = this.form.value.email;

    if (!email) {
      return;
    }

    this.apiService.authenticationApi.forgotPassword(email).pipe(
      map(result => result.data),
    ).subscribe(() => {
      this.router.navigate([ROUTE_UNAUTHENTICATED_MAIN, ROUTE_HOME]);
    });
  }

}
