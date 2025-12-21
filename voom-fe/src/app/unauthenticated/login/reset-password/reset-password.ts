import {Component, inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {ValueInputString} from "../../../shared/value-input/value-input-string/value-input-string";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {ROUTE_HOME} from '../../../core/layout/home/home';

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

  private router = inject(Router);

  form = new FormGroup({
    password1: new FormControl<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(255)]),
    password2: new FormControl<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(255)]),
  });

  submit() {
    this.router.navigate([ROUTE_HOME]);
  }

}
