import { Component } from '@angular/core';

import { MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ValueInputString } from '../../value-input/value-input-string/value-input-string';

@Component({
  selector: 'app-change-password-dialog',
  imports: [
    CommonModule,
    MatDialogModule,
    ValueInputString,
  ],
  templateUrl: './change-password-dialog.html',
  styleUrl: './change-password-dialog.css',
})
export class ChangePasswordDialog {

}
