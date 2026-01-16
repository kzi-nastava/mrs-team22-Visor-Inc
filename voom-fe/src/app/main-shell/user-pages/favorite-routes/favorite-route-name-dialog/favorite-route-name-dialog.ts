import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-favorite-route-name-dialog',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './favorite-route-name-dialog.html',
  styleUrls: ['./favorite-route-name-dialog.css'],
})
export class FavoriteRouteNameDialog {
  nameControl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(2)],
  });

  constructor(
    private dialogRef: MatDialogRef<FavoriteRouteNameDialog>,
    @Inject(MAT_DIALOG_DATA) public data?: { initialName?: string }
  ) {
    if (data?.initialName) {
      this.nameControl.setValue(data.initialName);
    }
  }

  cancel() {
    this.dialogRef.close(null);
  }

  save() {
    if (this.nameControl.invalid) return;
    this.dialogRef.close(this.nameControl.value.trim());
  }
}
