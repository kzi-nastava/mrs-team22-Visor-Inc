import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl, Validators, FormGroup } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatSelectModule } from '@angular/material/select';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Footer } from '../../../core/layout/footer/footer';
import { ValueInputString } from '../../../shared/value-input/value-input-string/value-input-string';
import { Header } from '../../../core/layout/header/header';
import { ChangePasswordDialog } from '../../../shared/popup/change-password-dialog/change-password-dialog';

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
    Footer,
    ValueInputString,
    Header,
    MatDialogModule,
    ChangePasswordDialog,
  ],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.css',
})
export class UserProfile {
  constructor(private dialog: MatDialog) {}

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
}
