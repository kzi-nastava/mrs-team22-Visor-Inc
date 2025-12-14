import {Component, Input} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {MatError, MatFormField, MatInput, MatLabel} from '@angular/material/input';

@Component({
  selector: 'app-value-input-string',
  imports: [
    MatFormField,
    MatError,
    MatLabel,
    ReactiveFormsModule,
    MatInput
  ],
  templateUrl: './value-input-string.html',
  styleUrl: './value-input-string.css',
})
export class ValueInputString {

  @Input({ required: true }) formControl!: FormControl<string | null>;
  @Input({ required: true }) label!: string;

  getErrors(): string[] {
    return this.formControl.errors ? Object.values(this.formControl.errors).map(err => String(err)) : [];
  }

}
