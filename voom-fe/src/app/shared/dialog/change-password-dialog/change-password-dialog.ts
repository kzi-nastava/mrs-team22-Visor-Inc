import { Component } from '@angular/core';

import { MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ValueInputString } from '../../value-input/value-input-string/value-input-string';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';


@Component({
  selector: 'app-change-password-dialog',
  imports: [
    CommonModule,
    MatDialogModule,
    ValueInputString,
    ReactiveFormsModule,
  ],
  templateUrl: './change-password-dialog.html',
  styleUrl: './change-password-dialog.css',
})
export class ChangePasswordDialog {
  passwordForm = new FormGroup({
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
  });

}
