import { Component } from '@angular/core';

import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ValueInputString } from '../../value-input/value-input-string/value-input-string';
import {
  FormControl,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { UserProfileApi } from '../../../authenticated/user/user-profile/user-profile.api';

@Component({
  selector: 'app-change-password-dialog',
  imports: [
    CommonModule,
    MatDialogModule,
    ValueInputString,
    ReactiveFormsModule,
    MatSnackBarModule,
  ],
  templateUrl: './change-password-dialog.html',
  styleUrl: './change-password-dialog.css',
})
export class ChangePasswordDialog {
  constructor(
    private userProfileApi: UserProfileApi,
    private dialogRef: MatDialogRef<ChangePasswordDialog>,
    private snackBar: MatSnackBar
  ) {}

  private passwordsMatch(): boolean {
    return this.passwordForm.value.password1 === this.passwordForm.value.password2;
  }

  onSave(): void {
    if (this.passwordForm.invalid || !this.passwordsMatch()) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    const body = {
      newPassword: this.passwordForm.value.password1!,
      confirmPassword: this.passwordForm.value.password2!,
    };

    this.userProfileApi.updatePassword(body).subscribe({
      next: () => {
        this.snackBar.open('Password successfully changed', 'OK', {
          duration: 3000,
          panelClass: ['snackbar-success'],
          horizontalPosition: 'right',
          verticalPosition: 'bottom',
        });

        this.dialogRef.close(true);
      },
      error: () => {
        this.snackBar.open('Failed to change password', 'Dismiss', {
          duration: 4000,
          panelClass: ['snackbar-error'],
        });
      },
    });
  }

  passwordForm = new FormGroup(
    {
      password1: new FormControl<string>('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(255),
      ]),
      password2: new FormControl<string>('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(255),
      ]),
    },
    { validators: passwordsMatchValidator }
  );
}

function passwordsMatchValidator(control: AbstractControl): ValidationErrors | null {
  const p1 = control.get('password1')?.value;
  const p2 = control.get('password2')?.value;

  if (!p1 || !p2) {
    return null;
  }

  return p1 === p2 ? null : { passwordsMismatch: true };
}
