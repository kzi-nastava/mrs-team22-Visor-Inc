import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { Header } from '../../../core/layout/header-kt1/header-kt1';
import { Footer } from '../../../core/layout/footer/footer';
import { ValueInputString } from '../../../shared/value-input/value-input-string/value-input-string';
import { ValueInputFile } from '../../../shared/value-input/value-input-file/value-input-file';
import { RegisterDriverApi } from './register-driver.api';

export const ROUTE_ADMIN_REGISTER_DRIVER = 'admin/register-driver';

@Component({
  selector: 'app-admin-register-driver',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatSelectModule,
    MatCheckbox,
    MatSnackBarModule,
    Header,
    Footer,
    ValueInputString,
    ValueInputFile,
  ],
  templateUrl: './register-driver.html',
})
export class AdminRegisterDriver {
  constructor(
    private api: RegisterDriverApi,
    private snackBar: MatSnackBar
  ) {}

  form = new FormGroup({
    profileImage: new FormControl<File | null>(null),

    firstName: new FormControl('', [Validators.required, Validators.minLength(2)]),
    lastName: new FormControl('', [Validators.required, Validators.minLength(2)]),

    phone: new FormControl('', Validators.required),
    address: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email]),

    vehicleModel: new FormControl('', Validators.required),
    vehicleType: new FormControl<'STANDARD' | 'LUXURY' | 'VAN' | null>(null, Validators.required),
    licensePlate: new FormControl('', Validators.required),
    seats: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(1),
      Validators.max(8),
    ]),
    babyTransportAllowed: new FormControl(false),
    petsAllowed: new FormControl(false),
  });

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const v = this.form.getRawValue();

    const payload = new FormData();
    payload.append('firstName', v.firstName!);
    payload.append('lastName', v.lastName!);
    payload.append('phoneNumber', v.phone!);
    payload.append('address', v.address!);
    payload.append('email', v.email!);

    payload.append('vehicle.model', v.vehicleModel!);
    payload.append('vehicle.type', v.vehicleType!);
    payload.append('vehicle.licensePlate', v.licensePlate!);
    payload.append('vehicle.numberOfSeats', String(v.seats!));
    payload.append('vehicle.babySeat', String(v.babyTransportAllowed!));
    payload.append('vehicle.petFriendly', String(v.petsAllowed!));

    if (v.profileImage) {
      payload.append('profileImage', v.profileImage);
    }

    this.api.registerDriver(payload).subscribe({
      next: () => {
        this.snackBar.open(
          'Driver registered. Activation email sent.',
          'OK',
          { duration: 3500 }
        );
        this.form.reset();
      },
      error: () => {
        this.snackBar.open(
          'Failed to register driver',
          'Dismiss',
          { duration: 4000 }
        );
      },
    });
  }
}
