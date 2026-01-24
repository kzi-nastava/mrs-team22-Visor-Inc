import { Component, inject } from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButton } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ValueInputString } from '../../../shared/value-input/value-input-string/value-input-string';
import { ActivateProfileApi } from './activate-profile.api';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatIconModule } from '@angular/material/icon';

export const ROUTE_ACTIVATE_PROFILE = 'voom/activate';

function passwordStrengthValidator(control: AbstractControl): ValidationErrors | null {
  const value = control.value as string;
  if (!value) return null;

  const hasMinLength = value.length >= 8;
  const hasUppercase = /[A-Z]/.test(value);
  const hasLowercase = /[a-z]/.test(value);
  const hasNumber = /[0-9]/.test(value);

  const valid = hasMinLength && hasUppercase && hasLowercase && hasNumber;

  return valid
    ? null
    : {
        passwordStrength: {
          hasMinLength,
          hasUppercase,
          hasLowercase,
          hasNumber,
        },
      };
}

function passwordsMatchValidator(group: AbstractControl): ValidationErrors | null {
  const p1 = group.get('password1')?.value;
  const p2 = group.get('password2')?.value;
  return p1 && p2 && p1 !== p2 ? { passwordsMismatch: true } : null;
}

@Component({
  selector: 'app-activate-profile',
  imports: [
    ValueInputString,
    ReactiveFormsModule,
    MatButton,
    MatSnackBarModule,
    MatTooltipModule,
    MatIconModule,
  ],
  templateUrl: './activate-profile.html',
})
export class ActivateProfile {
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  token!: string;

  form = new FormGroup(
    {
      password1: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required, Validators.maxLength(255), passwordStrengthValidator],
      }),
      password2: new FormControl('', {
        nonNullable: true,
        validators: [Validators.required, Validators.maxLength(255)],
      }),
    },
    { validators: passwordsMatchValidator }
  );

  get passwordTooltipText(): string {
    const e = this.passwordStrengthErrors;

    return [
      `${e?.hasMinLength ? '✓' : '•'} At least 8 characters`,
      `${e?.hasUppercase ? '✓' : '•'} One uppercase letter`,
      `${e?.hasLowercase ? '✓' : '•'} One lowercase letter`,
      `${e?.hasNumber ? '✓' : '•'} One number`,
    ].join('\n');
  }

  constructor(private api: ActivateProfileApi, private snackBar: MatSnackBar) {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (token) this.token = token;
  }

  get passwordStrengthErrors() {
    return this.form.controls.password1.errors?.['passwordStrength'] ?? null;
  }

  submit() {
    if (this.form.invalid) return;

    this.api
      .activateProfile({
        token: this.token,
        password: this.form.controls.password1.value,
        confirmPassword: this.form.controls.password2.value,
      })
      .subscribe({
        next: () => {
          this.snackBar.open('Profile activated', 'OK', { duration: 2000 });
          setTimeout(() => this.router.navigateByUrl('/login'), 500);
        },
        error: () => {
          this.snackBar.open('Activation failed', 'Dismiss', { duration: 3000 });
        },
      });
  }
}
