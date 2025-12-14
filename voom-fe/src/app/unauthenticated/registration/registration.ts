import {Component, inject} from '@angular/core';
import {MatButton} from '@angular/material/button';
import {ValueInputString} from '../../shared/value-input/value-input-string/value-input-string';
import {Router} from '@angular/router';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ROUTE_LOGIN} from '../login/login';

export const ROUTE_REGISTRATION = 'registration';

@Component({
  selector: 'app-registration',
  imports: [
    MatButton,
    ValueInputString,
    ReactiveFormsModule
  ],
  templateUrl: './registration.html',
  styleUrl: './registration.css',
})
export class Registration {

  private router = inject(Router);

  form = new FormGroup({
    email: new FormControl<string>('', [Validators.required, Validators.email, Validators.maxLength(255)]),
    password1: new FormControl<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(255)]),
    password2: new FormControl<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(255)]),
    firstName: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    lastName: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    address: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
  });

  register() {
    console.log(this.form.value);
  }

  login() {
    this.router.navigate([ROUTE_LOGIN]);
  }

}
