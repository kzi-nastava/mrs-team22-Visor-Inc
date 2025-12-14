import {Component, forwardRef, Input} from '@angular/core';
import {MatError, MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR, ReactiveFormsModule} from "@angular/forms";
import {Subject, takeUntil} from 'rxjs';

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
export class ValueInputNumeric implements ControlValueAccessor{

  formControl = new FormControl<number | null>(null);
  @Input({ required: true }) label!: string;

  destroy$ = new Subject<void>();

  private onChange = (v: number) => {};
  private onTouched = () => {};

  getErrors(): string[] {
    return Object.keys(this.formControl.errors ?? [])
      .filter((key) => (this.formControl.errors as any ?? [])[key])
  }

  registerOnChange(fn: any): void {
    this.formControl.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(fn)
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    if (isDisabled) {
      this.formControl.disable();
    } else {
      this.formControl.enable();
    }
  }

  writeValue(value: number): void {
    if (!value) {
      this.formControl.reset();
      this.formControl.enable({emitEvent: false});
    } else {
      this.formControl.setValue(value);
      this.formControl.disable({emitEvent: false});
    }
  }


}
