import {Component, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatTimepicker, MatTimepickerInput, MatTimepickerToggle} from '@angular/material/timepicker';

@Component({
  selector: 'app-value-input-time',
  imports: [
    MatLabel,
    MatFormField,
    MatError,
    MatTimepickerInput,
    MatTimepickerToggle,
    MatTimepicker,
    MatInput
  ],
  templateUrl: './value-input-time.html',
  styleUrl: './value-input-time.css',
})
export class ValueInputTime {
  @Input({ required: true }) formControl!: FormControl<string>;
  @Input({ required: true }) label!: string;

  getErrors(): string[] {
    return this.formControl.errors ? Object.values(this.formControl.errors).map(err => String(err)) : [];
  }

}
