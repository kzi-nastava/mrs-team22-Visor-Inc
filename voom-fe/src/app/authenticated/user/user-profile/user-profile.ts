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
import {Footer} from '../../../core/layout/footer/footer';
import {ValueInputString} from '../../../shared/value-input/value-input-string/value-input-string';
import {Header} from '../../../core/layout/header-kt1/header-kt1';
import {ChangePasswordDialog} from '../../../shared/dialog/change-password-dialog/change-password-dialog';
import {MatCheckbox} from '@angular/material/checkbox';
import { UserProfileApi } from './user-profile.api';

export const ROUTE_USER_PROFILE = 'profile';

export type UserProfileResponseDto = {
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
};

export type UpdateUserProfileRequestDto = {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
};

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
    Footer,
    ValueInputString,
    Header,
    MatDialogModule,
    MatCheckbox,
    // ChangePasswordDialog,
  ],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.css',
})
export class UserProfile {
  constructor(private dialog: MatDialog, private profileApi: UserProfileApi) {}

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
      error: (err) => {
        console.error('Failed to load profile', err);
      },
    });
  }

  submit() {
  if (this.profileForm.invalid) {
    this.profileForm.markAllAsTouched();
    return;
  }

  const v = this.profileForm.getRawValue();

  this.profileApi.updateProfile({
    firstName: v.firstName ?? '',
    lastName: v.lastName ?? '',
    phoneNumber: v.phone ?? '',
    address: v.address ?? '',
  }).subscribe({
    next: () => console.log('Profile updated'),
    error: console.error,
  });
}

}
