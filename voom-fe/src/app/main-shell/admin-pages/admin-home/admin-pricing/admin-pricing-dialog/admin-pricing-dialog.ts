import {Component, inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import ApiService from '../../../../../shared/rest/api-service';
import {MatDialogRef} from '@angular/material/dialog';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ValueInputNumeric} from '../../../../../shared/value-input/value-input-numeric/value-input-numeric';
import {ValueInputString} from '../../../../../shared/value-input/value-input-string/value-input-string';

@Component({
  selector: 'app-admin-pricing-dialog',
  imports: [
    MatButton,
    ReactiveFormsModule,
    ValueInputNumeric,
    ValueInputString
  ],
  templateUrl: './admin-pricing-dialog.html',
  styleUrl: './admin-pricing-dialog.css',
})
export class AdminPricingDialog {

  pricingForm = new FormGroup({
    type: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]),
    price: new FormControl<number>(0, [Validators.required]),
  })

  private apiService = inject(ApiService);

  constructor(private dialogRef: MatDialogRef<AdminPricingDialog>) {
  }

  protected cancel() {
    this.dialogRef.close();
  }

  protected submit() {
    this.dialogRef.close();
  }
}
