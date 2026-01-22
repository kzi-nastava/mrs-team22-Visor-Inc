import {Component, inject, signal} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatDivider} from '@angular/material/list';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {MatIcon} from '@angular/material/icon';
import {ValueInputString} from '../../../../shared/value-input/value-input-string/value-input-string';
import ApiService from '../../../../shared/rest/api-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {map} from 'rxjs';
import {toSignal} from '@angular/core/rxjs-interop';
import {VehicleDto} from '../../../../shared/rest/vehicle/vehicle.model';
import {VehicleTypeDto} from '../../../../shared/rest/vehicle/vehicle-type.model';
import {ValueInputNumeric} from '../../../../shared/value-input/value-input-numeric/value-input-numeric';
import {DriverDto} from '../../../../shared/rest/driver/driver.model';
import {MatCheckbox} from '@angular/material/checkbox';

export const ROUTE_ADMIN_VEHICLES = "vehicles";

@Component({
  selector: 'app-admin-vehicles',
  imports: [
    FormsModule,
    MatCard,
    MatCardContent,
    MatCardTitle,
    MatDivider,
    MatDrawer,
    MatDrawerContainer,
    MatDrawerContent,
    MatIcon,
    ReactiveFormsModule,
    ValueInputString,
    ValueInputNumeric,
    MatCheckbox
  ],
  templateUrl: './admin-vehicles.html',
  styleUrl: './admin-vehicles.css',
})
export class AdminVehicles {

  vehicleForm = new FormGroup({
    driver: new FormControl({ value: '', disabled: true }, [Validators.required]),
    vehicleType: new FormControl<string>({ value: '', disabled: true }, Validators.required),
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

  vehicles$ = this.apiService.vehicleApi.getVehicles().pipe(
    map(response => response.data),
  );

  vehicleTypes$ = this.apiService.vehicleTypeApi.getVehicleTypes().pipe(
    map(response => response.data),
  );

  vehicles = toSignal(this.vehicles$);
  vehicleTypes = toSignal(this.vehicleTypes$);

  selectedVehicle = signal<VehicleDto | null>(null);
  selectedVehicleType = signal<VehicleTypeDto | null>(null);

  constructor() {
  }

  protected selectVehicle(vehicle: VehicleDto) {
    const vehicleTypes = this.vehicleTypes();

    if (!vehicleTypes || !vehicle) {
      return;
    }

    const vehicleType = vehicleTypes.find(vehicleType => vehicleType.id === vehicle.vehicleTypeId);

    if (!vehicleType) {
      return;
    }

    this.selectedVehicleType.set(vehicleType);
    this.selectedVehicle.set(vehicle);

    this.vehicleForm.setValue({
      driver: vehicle.driverId.toString(),
      vehicleType: vehicleType.type,
      year: vehicle.year,
      model: vehicle.model,
      licensePlate: vehicle.licensePlate,
      babySeat: vehicle.babySeat,
      petFriendly: vehicle.petFriendly,
      numberOfSeats: vehicle.numberOfSeats,
    });

    this.vehicleForm.get("driver")?.disable();
    this.vehicleForm.get("vehicleType")?.disable();
    this.vehicleForm.get("year")?.enable();
    this.vehicleForm.get("model")?.enable();
    this.vehicleForm.get("licensePlate")?.enable();
    this.vehicleForm.get("babySeat")?.enable();
    this.vehicleForm.get("petFriendly")?.enable();
    this.vehicleForm.get("numberOfSeats")?.enable();

  }

  protected addVehicle() {

  }

  protected saveVehicle() {

  }

  protected deleteVehicle() {

  }
}
