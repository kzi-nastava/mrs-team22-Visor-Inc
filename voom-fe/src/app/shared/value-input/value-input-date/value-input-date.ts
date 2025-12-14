import {Component, forwardRef, Input} from '@angular/core';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';
import {MatError, MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR} from '@angular/forms';
import {Subject, takeUntil} from 'rxjs';

@Component({
  selector: 'app-value-input-date',
  imports: [
    MatDatepickerInput,
    MatLabel,
    MatFormField,
    MatDatepickerToggle,
    MatDatepicker,
    MatError,
    MatInput,
    MatSuffix
  ],
  templateUrl: './value-input-date.html',
  styleUrl: './value-input-date.css',
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => ValueInputDate),
    multi: true
  }]
})
export class ValueInputDate implements ControlValueAccessor{

  formControl = new FormControl<Date | null>(null);
  @Input({ required: true }) label!: string;

  destroy$ = new Subject<void>();

  private onChange = (v: Date) => {};
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

  writeValue(value: Date): void {
    if (!value) {
      this.formControl.reset();
      this.formControl.enable({emitEvent: false});
    } else {
      this.formControl.setValue(value);
      this.formControl.disable({emitEvent: false});
    }
  }
}
