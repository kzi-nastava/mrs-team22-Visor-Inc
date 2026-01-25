import {Component, inject, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {MatDivider} from '@angular/material/list';
import {ValueInputString} from '../../../shared/value-input/value-input-string/value-input-string';
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms';
import ApiService from '../../../shared/rest/api-service';

export type ArrivalDialogResult = 'START' | 'CANCEL';

@Component({
  selector: 'app-arrival-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatDivider,
    ValueInputString,
    ReactiveFormsModule,
  ],
  templateUrl: './arrival-dialog.html',
})
export class ArrivalDialog {

  cancelFormControl = new FormControl<string>('', Validators.required);

  private apiService = inject(ApiService);

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
