import {Component, Input} from '@angular/core';
import {MatError, MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {FormControl, ReactiveFormsModule} from "@angular/forms";

@Component({
  selector: 'app-value-input-numeric',
    imports: [
        MatError,
        MatFormField,
        MatInput,
        MatLabel,
        ReactiveFormsModule
    ],
  templateUrl: './value-input-numeric.html',
  styleUrl: './value-input-numeric.css',
})
export class ValueInputNumeric {

  @Input({ required: true }) formControl!: FormControl<number>;
  @Input({ required: true }) label!: string;

  getErrors(): string[] {
    return this.formControl.errors ? Object.values(this.formControl.errors).map(err => String(err)) : [];
  }

}
