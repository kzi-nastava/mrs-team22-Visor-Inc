import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

export type FinishRideDialogResult = 'FINISH';

@Component({
  selector: 'app-finish-ride-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
  templateUrl: './finish-ride-dialog.html',
})
export class FinishRideDialog {
  constructor(
    private dialogRef: MatDialogRef<FinishRideDialog, FinishRideDialogResult>,
    @Inject(MAT_DIALOG_DATA)
    public data?: {
      dropoffAddress?: string;
    },
  ) {}

  finish() {
    this.dialogRef.close('FINISH');
  }
}