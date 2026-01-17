import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';

import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {MatSelectModule} from '@angular/material/select';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {ValueInputString} from '../../../shared/value-input/value-input-string/value-input-string';
import {ChangePasswordDialog} from '../../../shared/dialog/change-password-dialog/change-password-dialog';
import {MatCheckbox} from '@angular/material/checkbox';
import {UserProfileApi} from './user-profile.api';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';

export const ROUTE_USER_PROFILE = 'profile';

@Component({
  selector: 'app-user-profile',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatSelectModule,
    ValueInputString,
    MatDialogModule,
    MatCheckbox,
    MatSnackBarModule,
  ],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.css',
})
export class UserProfile {
  constructor(
    private dialog: MatDialog,
    private profileApi: UserProfileApi,
    private snackBar: MatSnackBar
  ) {}

  userRole: 'Driver' | 'User' | 'Admin' = 'Driver';

  openChangePasswordDialog(): void {
    this.dialog.open(ChangePasswordDialog, {
      width: '420px',
      autoFocus: false,
      panelClass: 'rounded-dialog',
    });
  }

  profileForm = new FormGroup({
    firstName: new FormControl<string>('', [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(255),
    ]),
    lastName: new FormControl<string>('', [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(255),
    ]),
    phone: new FormControl<string>('', [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(55),
    ]),
    address: new FormControl<string>('', [
      Validators.required,
      Validators.minLength(2),
      Validators.maxLength(255),
    ]),
    email: new FormControl<string>('', [
      Validators.required,
      Validators.email,
      Validators.maxLength(255),
    ]),
  });

  vehicleForm = new FormGroup({
    model: new FormControl<string>('', Validators.required),
    vehicleType: new FormControl<string | null>(null, Validators.required),
    licensePlate: new FormControl<string>('', Validators.required),
    seats: new FormControl<number | null>(null, Validators.required),
    babyTransportAllowed: new FormControl<boolean>(false),
    petsAllowed: new FormControl<boolean>(false),
  });

  ngOnInit(): void {
    this.profileApi.getProfile().subscribe({
      next: (profile) => {
        this.profileForm.patchValue({
          firstName: profile.firstName,
          lastName: profile.lastName,
          phone: profile.phoneNumber,
          address: profile.address,
          email: profile.email,
        });

        this.profileForm.controls.email.disable();
      },
    });

    if (this.userRole === 'Driver') {
      this.profileApi.getMyVehicle().subscribe({
        next: (vehicle) => {
          console.log('Loaded vehicle info:', vehicle);
          this.vehicleForm.patchValue({
            model: vehicle.model,
            vehicleType: vehicle.vehicleType,
            licensePlate: vehicle.licensePlate,
            seats: vehicle.numberOfSeats,
            babyTransportAllowed: vehicle.babySeat,
            petsAllowed: vehicle.petFriendly,
          });
        },
        error: (err) => {
          console.error('Failed to load vehicle info', err);
        },
      });
    }
  }

  submit() {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    const v = this.profileForm.getRawValue();

    this.profileApi
      .updateProfile({
        firstName: v.firstName ?? '',
        lastName: v.lastName ?? '',
        phoneNumber: v.phone ?? '',
        address: v.address ?? '',
      })
      .subscribe({
        next: () => {
          this.snackBar.open('Profile successfully updated', 'OK', {
            duration: 3000,
            panelClass: ['snackbar-success'],
            horizontalPosition: 'right',
            verticalPosition: 'bottom',
          });
        },
        error: () => {
          this.snackBar.open('Failed to update profile', 'Dismiss', {
            duration: 4000,
            panelClass: ['snackbar-error'],
            horizontalPosition: 'right',
            verticalPosition: 'bottom',
          });
        },
      });
  }

  vehicleSubmit() {
    if (this.vehicleForm.invalid) {
      this.vehicleForm.markAllAsTouched();
      return;
    }

    const v = this.vehicleForm.getRawValue();

    this.profileApi
      .updateMyVehicle({
        model: v.model ?? '',
        vehicleType: v.vehicleType as 'STANDARD' | 'LUXURY' | 'VAN',
        licensePlate: v.licensePlate ?? '',
        numberOfSeats: v.seats ?? 0,
        babySeat: v.babyTransportAllowed ?? false,
        petFriendly: v.petsAllowed ?? false,
      })
      .subscribe({
        next: () => {
          this.snackBar.open('Request for vehicle update sent', 'OK', {
            duration: 3000,
            panelClass: ['snackbar-success'],
            horizontalPosition: 'right',
            verticalPosition: 'bottom',
          });
        },
        error: () => {
          this.snackBar.open('Failed to update vehicle', 'Dismiss', {
            duration: 4000,
            panelClass: ['snackbar-error'],
            horizontalPosition: 'right',
            verticalPosition: 'bottom',
          });
        },
      });
  }
}
