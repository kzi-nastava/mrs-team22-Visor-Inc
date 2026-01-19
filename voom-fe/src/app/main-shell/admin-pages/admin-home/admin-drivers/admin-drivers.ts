import {Component, inject, signal} from '@angular/core';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {MatDivider} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ValueInputString} from '../../../../shared/value-input/value-input-string/value-input-string';
import {ValueInputDate} from '../../../../shared/value-input/value-input-date/value-input-date';
import ApiService from '../../../../shared/rest/api-service';
import {map} from 'rxjs';
import {toSignal} from '@angular/core/rxjs-interop';
import {DriverSummaryDto} from '../../../../shared/rest/home/home.model';

export const ROUTE_ADMIN_DRIVERS = "drivers";

@Component({
  selector: 'app-admin-drivers',
  imports: [
    MatCard,
    MatDrawer,
    MatDrawerContainer,
    MatDrawerContent,
    ValueInputString,
    MatCardContent,
    MatCardTitle,
    MatDivider,
    MatIcon,
    ValueInputDate,
    ReactiveFormsModule
  ],
  templateUrl: './admin-drivers.html',
  styleUrl: './admin-drivers.css',
})
export class AdminDrivers {

  driverGeneralForm = new FormGroup({
    firstName: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    lastName: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    birthDate: new FormControl<Date | null>(null, [Validators.required]),
    email: new FormControl<string>('', [Validators.required, Validators.email, Validators.maxLength(255)]),
    address: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    phoneNumber: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(55)]),
  });

  private apiService = inject(ApiService);

  drivers$ = this.apiService.rideApi.getActiveDrivers().pipe(
    map(response => response.data),
  )

  drivers = toSignal(this.drivers$);
  selectedDriver = signal<DriverSummaryDto | null>(null)

  constructor() {
  }

  protected openProfilePictureDialog() {

  }

  protected saveGeneralInfo() {

  }

  protected selectDriver(driver: DriverSummaryDto) {
    console.log(driver);
    this.selectedDriver.set(driver);
    this.driverGeneralForm.patchValue(driver);
  }
}
