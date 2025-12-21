import {Component, inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {ValueInputString} from "../../../shared/value-input/value-input-string/value-input-string";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {ROUTE_RESET_PASSWORD} from '../reset-password/reset-password';

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

  private router = inject(Router);

  form = new FormGroup({
    email: new FormControl<string>('', [Validators.required, Validators.email, Validators.maxLength(255)]),
  });

  submit() {
    this.router.navigate([ROUTE_RESET_PASSWORD]);
  }

}
