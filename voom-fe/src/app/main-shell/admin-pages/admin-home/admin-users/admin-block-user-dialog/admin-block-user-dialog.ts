import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

export interface AdminBlockUserDialogData {
  userId: number;
  fullName: string;
}


@Component({
  selector: 'app-admin-block-user-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './admin-block-user-dialog.html',
  styleUrls: ['./admin-block-user-dialog.css']
})
export class AdminBlockUserDialog {

  form = new FormGroup({
    reason: new FormControl<string>('', [
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(500)
    ])
  });

  constructor(
    private dialogRef: MatDialogRef<AdminBlockUserDialog>,
    @Inject(MAT_DIALOG_DATA) public data: AdminBlockUserDialogData
  ) {}

  close(): void {
    this.dialogRef.close();
  }

  confirm(): void {
    if (this.form.invalid) return;

    this.dialogRef.close({
      userId: this.data.userId,
      reason: this.form.value.reason
    });
  }
}
