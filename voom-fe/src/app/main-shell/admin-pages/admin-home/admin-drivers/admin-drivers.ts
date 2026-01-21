import {Component, inject, signal} from '@angular/core';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {MatDivider} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ValueInputString} from '../../../../shared/value-input/value-input-string/value-input-string';
import {ValueInputDate} from '../../../../shared/value-input/value-input-date/value-input-date';
import ApiService from '../../../../shared/rest/api-service';
import {BehaviorSubject, map} from 'rxjs';
import {toSignal} from '@angular/core/rxjs-interop';
import {DriverSummaryDto} from '../../../../shared/rest/home/home.model';
import {DriverDto} from '../../../../shared/rest/driver/driver.model';
import {response} from 'express';
import {MatSnackBar} from '@angular/material/snack-bar';

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
  private snackBar = inject(MatSnackBar);

  drivers$ = this.apiService.driverApi.getDrivers().pipe(
    map(response => response.data),
  );

  drivers = toSignal(this.drivers$);
  selectedDriver = signal<DriverDto | null>(null)

  constructor() {
  }

  protected openProfilePictureDialog() {

  }

  protected saveGeneralInfo() {
    const driver = this.selectedDriver();

    if (!driver) {
      return;
    }

    const updatedDriverDto: DriverDto = {
      id: driver.id,
      firstName: this.driverGeneralForm.value.firstName!,
      lastName: this.driverGeneralForm.value.lastName!,
      birthDate: this.driverGeneralForm.value.birthDate!,
      email: this.driverGeneralForm.value.email!,
      address: this.driverGeneralForm.value.address!,
      phoneNumber: this.driverGeneralForm.value.phoneNumber!,
    }

    this.apiService.driverApi.updateDriver(driver.id!, updatedDriverDto).pipe(
      map(response => response.data),
    ).subscribe((driver) => {
      if (driver) {
        this.snackBar.open("Driver updated successfully");
        this.driverGeneralForm.patchValue(driver);
        //TODO on update update drivers$
      } else {
        this.snackBar.open("Driver update failed");
      }
    });
  }

  protected selectDriver(driver: DriverDto) {
    this.selectedDriver.set(driver);
    this.driverGeneralForm.patchValue(driver);
  }
}
