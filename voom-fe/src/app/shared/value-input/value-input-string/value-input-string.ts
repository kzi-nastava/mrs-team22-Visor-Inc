import {Component, forwardRef, Input, signal, SimpleChanges} from '@angular/core';
import {ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR, ReactiveFormsModule} from '@angular/forms';
import {MatError, MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {Subject, takeUntil} from 'rxjs';
import {MatIcon} from '@angular/material/icon';
import {MatIconButton} from '@angular/material/button';
import {MatTooltip} from '@angular/material/tooltip';

@Component({
  selector: 'app-value-input-string',
  imports: [
    MatFormField,
    MatError,
    MatLabel,
    ReactiveFormsModule,
    MatInput,
    MatIcon,
    MatSuffix,
    MatIconButton,
    MatTooltip
  ],
  templateUrl: './value-input-string.html',
  styleUrl: './value-input-string.css',
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => ValueInputString),
    multi: true
  }]
})
export class ValueInputString implements ControlValueAccessor{

  formControl = new FormControl<string>('');
  @Input({ required: true }) label!: string;
  @Input({ required: false }) hide: boolean = false;

  innerHide = signal<boolean>(false);
  destroy$ = new Subject<void>();

  private onChange = (v: string) => {};
  private onTouched = () => {};

  constructor() {
  }

  ngOnInit() {
    this.innerHide.set(this.hide);
  }

  getErrors(): string[] {
    return Object.keys(this.formControl.errors ?? [])
      .filter((key) => (this.formControl.errors as any ?? [])[key])
  }

  writeValue(value: string): void {
    if (!value) {
      this.formControl.reset();
      this.formControl.enable({emitEvent: false});
    } else {
      this.formControl.setValue(value);
      this.formControl.disable({emitEvent: false});
    }
  }

  registerOnChange(fn: any): void {
    this.formControl.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(fn)
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    if (isDisabled) {
      this.formControl.disable();
    } else {
      this.formControl.enable();
    }
  }

}
