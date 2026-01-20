import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

export type ArrivalDialogResult = 'START' | 'CANCEL';

@Component({
  selector: 'app-arrival-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
  ],
  templateUrl: './arrival-dialog.html',
})
export class ArrivalDialog {
  constructor(
    private dialogRef: MatDialogRef<ArrivalDialog, ArrivalDialogResult>,
    @Inject(MAT_DIALOG_DATA)
    public data?: {
      pickupAddress?: string;
    },
  ) {}

  cancel() {
    this.dialogRef.close('CANCEL');
  }

  start() {
    this.dialogRef.close('START');
  }
}
