import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { passwordsMatchValidator } from '../../../shared/dialog/change-password-dialog/change-password-dialog';
import { ValueInputString } from '../../../shared/value-input/value-input-string/value-input-string';
import { ROUTE_LOGIN } from '../../login/login';
import { MatButton } from '@angular/material/button';


export const ROUTE_ACTIVATE_PROFILE = 'activate';

@Component({
  selector: 'app-activate-profile',
  imports: [
    ValueInputString,
    ReactiveFormsModule,
    MatButton
  ],
  templateUrl: './activate-profile.html',
  styleUrl: './activate-profile.css',
})
export class ActivateProfile {

  private readonly baseUrl = 'http://localhost:8080/api';

  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private http = inject(HttpClient);

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
    }, { validators: passwordsMatchValidator }
  );

  constructor() {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (!token) {
      this.error = 'Invalid activation link';
      return;
    }
    this.token = token;
  }

  submit() {
    if (this.form.invalid) return;

    console.log('Activating profile with token:', this.token);

    this.http.post(`${this.baseUrl}/drivers/activation`, {
      token: this.token,
      password: this.form.value.password1,
      confirmPassword: this.form.value.password2,
    }).subscribe({
      next: () => {
        this.success = true;

        setTimeout(() => {
          this.router.navigate([ROUTE_LOGIN]);
        }, 1500);
      },
      error: (err) => {
        this.error = err.error?.message ?? 'Activation failed';
      },
    });
  }
}
