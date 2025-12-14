import {Component, Input} from '@angular/core';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {FormControl} from '@angular/forms';

@Component({
  selector: 'app-value-input-date',
  imports: [
    MatDatepickerInput,
    MatLabel,
    MatFormField,
    MatDatepickerToggle,
    MatDatepicker,
    MatError,
    MatInput
  ],
  templateUrl: './value-input-date.html',
  styleUrl: './value-input-date.css',
})
export class ValueInputDate {

  @Input({ required: true }) formControl!: FormControl<Date>;
  @Input({ required: true }) label!: string;

  getErrors(): string[] {
    return this.formControl.errors ? Object.values(this.formControl.errors).map(err => String(err)) : [];
  }
}
