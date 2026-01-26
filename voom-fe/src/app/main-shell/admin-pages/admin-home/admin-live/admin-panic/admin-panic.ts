import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from '@angular/material/dialog';
import {RideResponseDto} from '../../../../../shared/rest/ride/ride.model';
import {MatDivider} from '@angular/material/list';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-admin-panic',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDivider,
    MatDialogActions,
    MatButton
  ],
  templateUrl: './admin-panic.html',
  styleUrl: './admin-panic.css',
})
export class AdminPanic {

  constructor(private dialogRef: MatDialogRef<AdminPanic>, @Inject(MAT_DIALOG_DATA) public data: RideResponseDto) {
  }

  protected close() {
    this.dialogRef.close();
  }
}
