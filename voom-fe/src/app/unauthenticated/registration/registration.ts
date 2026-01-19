import {Component, inject} from '@angular/core';
import {MatButton} from '@angular/material/button';
import {ValueInputString} from '../../shared/value-input/value-input-string/value-input-string';
import {Router} from '@angular/router';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ROUTE_LOGIN} from '../login/login';
import {MatStep, MatStepLabel, MatStepper, MatStepperNext, MatStepperPrevious} from '@angular/material/stepper';
import {ValueInputDate} from '../../shared/value-input/value-input-date/value-input-date';
import {ROUTE_USER_PROFILE} from '../../main-shell/user-pages/user-profile/user-profile';
import {ValueInputFile} from '../../shared/value-input/value-input-file/value-input-file';
import ApiService from '../../shared/rest/api-service';
import {map} from 'rxjs';
import {ROUTE_UNAUTHENTICATED_MAIN} from '../unauthenticated-main';

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
    ValueInputDate,
    ValueInputFile
  ],
  templateUrl: './registration.html',
  styleUrl: './registration.css',
})
export class Registration {

  constructor(protected router: Router, protected apiService: ApiService) {
  }

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
    file: new FormControl<File | null>(null),
  });

  register() {
    const registrationData = {
      email: this.accountForm.value.email as string,
      password: this.accountForm.value.password1 as string,
      firstName: this.personalForm.value.firstName as string,
      lastName: this.personalForm.value.lastName as string,
      phoneNumber: this.contactForm.value.phoneNumber as string,
      address: this.contactForm.value.address as string,
      userType: 'USER',
    };

    this.apiService.authenticationApi.register(registrationData).pipe(
      map(response => response.data),
    ).subscribe(() => {
      this.router.navigate([ROUTE_UNAUTHENTICATED_MAIN, ROUTE_LOGIN]);
    });
  }

  login() {
    this.router.navigate([ROUTE_UNAUTHENTICATED_MAIN, ROUTE_LOGIN]);
  }

}
