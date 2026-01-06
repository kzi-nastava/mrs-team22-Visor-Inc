import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { passwordsMatchValidator } from '../../../shared/dialog/change-password-dialog/change-password-dialog';
import { ValueInputString } from '../../../shared/value-input/value-input-string/value-input-string';
import { ROUTE_LOGIN } from '../../login/login';
import { MatButton } from '@angular/material/button';
import { ActivateProfileApi } from './activate-profile.api';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

export const ROUTE_ACTIVATE_PROFILE = 'activate';

@Component({
  selector: 'app-activate-profile',
  imports: [ValueInputString, ReactiveFormsModule, MatButton, MatSnackBarModule],
  templateUrl: './activate-profile.html',
  styleUrl: './activate-profile.css',
})
export class ActivateProfile {
  constructor(private api: ActivateProfileApi, private snackBar: MatSnackBar) {
    this.api = api;
    this.snackBar = snackBar;

    const token = this.route.snapshot.queryParamMap.get('token');
    if (!token) {
      this.error = 'Invalid activation link';
      return;
    }
    this.token = token;
  }

  private router = inject(Router);
  private route = inject(ActivatedRoute);

  token!: string;
  error?: string;
  success = false;

  form = new FormGroup(
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

  submit() {
    if (this.form.invalid) return;

    this.api
      .activateProfile({
        token: this.token,
        password: this.form.value.password1!,
        confirmPassword: this.form.value.password2!,
      })
      .subscribe({
        next: () => {
          this.success = true;

          this.snackBar.open('Profile successfully activated. Redirecting to login...', 'OK', {
            duration: 3000,
            panelClass: ['snackbar-success'],
            horizontalPosition: 'right',
            verticalPosition: 'bottom',
          });

          setTimeout(() => {
            console.log('Navigating to login');
            this.router.navigateByUrl('/login');
          }, 500);
        },
        error: (err: HttpErrorResponse) => {
          this.snackBar.open('Failed to activate profile', 'Dismiss', {
            duration: 4000,
            panelClass: ['snackbar-error'],
          });

          this.error = err.error?.message ?? 'Activation failed';
        },
      });
  }
}
