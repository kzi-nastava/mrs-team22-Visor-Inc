import {Component, inject} from '@angular/core';
import {MatButton} from '@angular/material/button';
import {ValueInputString} from '../../shared/value-input/value-input-string/value-input-string';
import {Router} from '@angular/router';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ROUTE_LOGIN} from '../login/login';
import {MatStep, MatStepLabel, MatStepper, MatStepperNext, MatStepperPrevious} from '@angular/material/stepper';
import {ValueInputDate} from '../../shared/value-input/value-input-date/value-input-date';
import {ROUTE_USER_PROFILE} from '../../authenticated/user/user-profile/user-profile';

export const ROUTE_REGISTRATION = 'registration';

@Component({
  selector: 'app-registration',
  imports: [
    MatButton,
    ValueInputString,
    ReactiveFormsModule,
    MatStep,
    MatStepLabel,
    MatStepper,
    MatStepperNext,
    MatStepperPrevious,
    ValueInputDate
  ],
  templateUrl: './registration.html',
  styleUrl: './registration.css',
})
export class Registration {

  private router = inject(Router);

  personalForm = new FormGroup({
    firstName: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    lastName: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    birthDate: new FormControl<Date | null>(null, [Validators.required]),
  })

  accountForm = new FormGroup({
    email: new FormControl<string>('', [Validators.required, Validators.email, Validators.maxLength(255)]),
    password1: new FormControl<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(255)]),
    password2: new FormControl<string>('', [Validators.required, Validators.minLength(8), Validators.maxLength(255)]),
  })

  contactForm = new FormGroup({
    address: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    phoneNumber: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(55)]),
  });

  register() {
    console.log('register');
    this.router.navigate([ROUTE_USER_PROFILE]);
  }

  login() {
    this.router.navigate([ROUTE_LOGIN]);
  }

}
