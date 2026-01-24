import { Component, Inject, inject, signal } from '@angular/core';
import { MatButton } from "@angular/material/button";
import ApiService from '../../../../../shared/rest/api-service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { catchError, map, of } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { VehicleTypeDto } from '../../../../../shared/rest/vehicle/vehicle-type.model';
import { CreateVehicleDto } from '../../../../../shared/rest/vehicle/vehicle.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatFormField, MatLabel } from '@angular/material/input';
import { MatOption } from '@angular/material/core';
import { MatSelect } from '@angular/material/select';
import { ValueInputNumeric } from '../../../../../shared/value-input/value-input-numeric/value-input-numeric';
import { ValueInputString } from '../../../../../shared/value-input/value-input-string/value-input-string';
import { DriverDto } from '../../../../../shared/rest/driver/driver.model';
import { UserProfileDto } from '../../../../../shared/rest/user/user.model';

@Component({
  selector: 'app-admin-vehicles-dialog',
  imports: [
    MatButton,
    FormsModule,
    MatCheckbox,
    MatFormField,
    MatLabel,
    MatOption,
    MatSelect,
    ReactiveFormsModule,
    ValueInputNumeric,
    ValueInputString
  ],
  templateUrl: './admin-vehicles-dialog.html',
  styleUrl: './admin-vehicles-dialog.css',
})
export class AdminVehiclesDialog {

  vehicleForm = new FormGroup({
    vehicleType: new FormControl<VehicleTypeDto | null>({ value: null, disabled: false }, Validators.required),
    year: new FormControl<number | null>({ value: null, disabled: false }, [
      Validators.required,
      Validators.min(1900),
      Validators.max(new Date().getFullYear() + 1)
    ]),
    model: new FormControl<string>({ value: '', disabled: false }, [
      Validators.required,
      Validators.minLength(2)
    ]),
    licensePlate: new FormControl<string>({ value: '', disabled: false }, [
      Validators.required,
    ]),
    babySeat: new FormControl<boolean>({ value: false, disabled: false }, { nonNullable: true }),
    petFriendly: new FormControl<boolean>({ value: false, disabled: false }, { nonNullable: true }),
    numberOfSeats: new FormControl<number | null>({ value: null, disabled: false }, [
      Validators.required,
      Validators.min(1),
      Validators.max(20)
    ])
  });

  private apiService = inject(ApiService);
  private snackBar = inject(MatSnackBar);

  vehicleTypes$ = this.apiService.vehicleTypeApi.getVehicleTypes().pipe(
    map(response => response.data),
  );

  driver = signal<DriverDto | null>(null);
  vehicleTypes = toSignal(this.vehicleTypes$);

  constructor(private dialogRef: MatDialogRef<AdminVehiclesDialog>, @Inject(MAT_DIALOG_DATA) private data: UserProfileDto) {
    this.apiService.driverApi.createDriver({userId: this.data.id}).pipe(
      map(response => response.data),
      catchError(error => {
        this.snackBar.open(error, '', {horizontalPosition: "right", duration : 3000});
        return of(null);
      }),
    ).subscribe((driver) => {
      if (driver) {
        this.snackBar.open("User updated successfully", '', {horizontalPosition: "right", duration : 3000});
        this.driver.set(driver);
      } else {
        this.snackBar.open("User update failed",'', {horizontalPosition: "right", duration : 3000});
      }
    });
  }

  protected cancel() {
    this.dialogRef.close();
  }

  protected submit() {
    const formValues = this.vehicleForm.value;

    const vehicleTypeId = formValues.vehicleType?.id;
    const driver = this.driver();

    console.log(formValues, vehicleTypeId, driver)

    if (!vehicleTypeId || !driver) {
      return;
    }

    const dto: CreateVehicleDto = {
      driverId: driver!.id,
      vehicleTypeId: vehicleTypeId,
      year: formValues.year!,
      model: formValues.model!,
      licensePlate: formValues.licensePlate!,
      babySeat: !!formValues.babySeat,
      petFriendly: !!formValues.petFriendly,
      numberOfSeats: formValues.numberOfSeats!,
    };

    this.apiService.vehicleApi.createVehicle(dto).pipe(
      map(response => response.data),
      catchError(error => {
        this.snackBar.open(error, '', {horizontalPosition: "right", duration : 3000});
        return of(null);
      }),
    ).subscribe((vehicle) => {
      this.dialogRef.close(vehicle);
    });
  }

}
