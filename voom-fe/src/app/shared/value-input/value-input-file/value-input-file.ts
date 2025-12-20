import {Component, forwardRef, Input} from '@angular/core';
import {MatError, MatLabel} from "@angular/material/input";
import {ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR, ReactiveFormsModule} from "@angular/forms";
import {Subject, takeUntil} from 'rxjs';

@Component({
  selector: 'app-value-input-file',
  imports: [
    MatError,
    MatLabel,
    ReactiveFormsModule
  ],
  templateUrl: './value-input-file.html',
  styleUrl: './value-input-file.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => ValueInputFile),
      multi: true,
    },
  ],
})
export class ValueInputFile implements ControlValueAccessor {

  formControl = new FormControl<File | null>(null);
  previewUrl: string | null = null;
  @Input({ required: true }) label!: string;

  destroy$ = new Subject<void>();

  private onChange = (v: number) => {};
  private onTouched = () => {};

  getErrors() {
    return Object.keys(this.formControl.errors ?? [])
      .filter((key) => (this.formControl.errors as any ?? [])[key]);
  }

  registerOnChange(fn: any): void {
    this.formControl.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(fn);
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

  writeValue(file: File): void {
    if (!file) {
      this.previewUrl = null;
      return;
    }
    this.formControl.patchValue(file);
    this.previewFile(file);
  }

  previewFile(file: File): void {
    this.previewUrl = URL.createObjectURL(file);
  }

}
