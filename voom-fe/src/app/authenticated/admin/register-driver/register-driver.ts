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
  constructor(private api: RegisterDriverApi, private snackBar: MatSnackBar) {}

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

    const payload = {
      firstName: v.firstName!,
      lastName: v.lastName!,
      phoneNumber: v.phone!,
      address: v.address!,
      email: v.email!,
      vehicle: {
        model: v.vehicleModel!,
        vehicleType: v.vehicleType!,
        licensePlate: v.licensePlate!,
        numberOfSeats: v.seats!,
        babySeat: v.babyTransportAllowed!,
        petFriendly: v.petsAllowed!,
      },
    };

    // if (v.profileImage) {
    //   payload.append('profileImage', v.profileImage);
    // }

    console.log('Registering driver with data:', payload);

    this.api.registerDriver(payload).subscribe({
      next: () => {
        this.snackBar.open('Driver registered. Activation email sent.', 'OK', { duration: 3500 });
        this.form.reset({
          babyTransportAllowed: false,
          petsAllowed: false,
        });
      },
      error: () => {
        this.snackBar.open('Failed to register driver', 'Dismiss', { duration: 4000 });
      },
    });
  }
}
