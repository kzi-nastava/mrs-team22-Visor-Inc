import {Component, Inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {RideResponseDto} from '../../rest/ride/ride.model';

@Component({
  selector: 'app-panic-dialog',
    imports: [
        MatButton,
        MatDialogActions,
        MatDialogContent,
        MatDialogTitle
    ],
  templateUrl: './panic-dialog.html',
  styleUrl: './panic-dialog.css',
})
export class PanicDialog {

  constructor(private dialogRef: MatDialogRef<PanicDialog>, @Inject(MAT_DIALOG_DATA) public data: RideResponseDto) {
  }

  protected close() {
    this.dialogRef.close();
  }
}
