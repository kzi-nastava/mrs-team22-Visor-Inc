import { Component } from '@angular/core';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {ValueInputString} from '../../../shared/value-input/value-input-string/value-input-string';
import {MatDivider} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {ValueInputDate} from '../../../shared/value-input/value-input-date/value-input-date';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';

export const ROUTE_ADMIN_DRIVERS = "drivers";

@Component({
  selector: 'app-admin-drivers',
  imports: [
    MatCard,
    MatDrawer,
    MatDrawerContainer,
    MatDrawerContent,
    ValueInputString,
    MatCardContent,
    MatCardTitle,
    MatDivider,
    MatIcon,
    ValueInputDate,
    ReactiveFormsModule
  ],
  templateUrl: './admin-drivers.html',
  styleUrl: './admin-drivers.css',
})
export class AdminDrivers {
  userGeneralForm = new FormGroup({
    firstName: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    lastName: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    birthDate: new FormControl<Date | null>(null, [Validators.required]),
    email: new FormControl<string>('', [Validators.required, Validators.email, Validators.maxLength(255)]),
    address: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    phoneNumber: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(55)]),
  });

  protected openProfilePictureDialog() {

  }

  protected saveGeneralInfo() {

  }
}
