import {Component, inject, signal} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatDivider} from '@angular/material/list';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {MatIcon} from '@angular/material/icon';
import {ValueInputString} from '../../../../shared/value-input/value-input-string/value-input-string';
import ApiService from '../../../../shared/rest/api-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import { BehaviorSubject, catchError, filter, map, merge, of, scan } from 'rxjs';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import {VehicleDto} from '../../../../shared/rest/vehicle/vehicle.model';
import {VehicleTypeDto} from '../../../../shared/rest/vehicle/vehicle-type.model';
import {ValueInputNumeric} from '../../../../shared/value-input/value-input-numeric/value-input-numeric';
import {DriverDto} from '../../../../shared/rest/driver/driver.model';
import {MatCheckbox} from '@angular/material/checkbox';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {AdminUsersDialog} from '../admin-users/admin-users-dialog/admin-users-dialog';
import {AdminVehiclesDialog} from './admin-vehicles-dialog/admin-vehicles-dialog';
import { UserProfileDto } from '../../../../shared/rest/user/user.model';

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

  initialVehicles$ = this.apiService.vehicleApi.getVehicles().pipe(
    map(response => response.data),
  );

  vehicleCreate$ = new BehaviorSubject<VehicleDto | null>(null);
  vehicleUpdate$ = new BehaviorSubject<VehicleDto | null>(null);

  vehicles$= merge(
    this.initialVehicles$.pipe(filter(vehicle => !!vehicle), map(response => { return {type:'initial', value: response} })),
    this.vehicleCreate$.asObservable().pipe(takeUntilDestroyed(), filter(vehicle => !!vehicle), map(response => { return {type:'create', value: [response]} })),
    this.vehicleUpdate$.asObservable().pipe(takeUntilDestroyed(), filter(vehicle => !!vehicle), map(response => { return {type:'update', value: [response]} })),
  ).pipe(
    takeUntilDestroyed(),
    scan((acc, obj) => {
      switch (obj.type) {
        case 'initial':
          return obj.value;
        case 'create':
          return [...acc, ...obj.value];
        case 'update':
          return [...acc.filter(vehicle => vehicle.id !== obj.value[0].id), obj.value[0]];
        default:
          return [];
      }
    }, [] as VehicleDto[]),
  );

  vehicleTypes$ = this.apiService.vehicleTypeApi.getVehicleTypes().pipe(
    map(response => response.data),
  );

  vehicles = toSignal(this.vehicles$);
  vehicleTypes = toSignal(this.vehicleTypes$);

  selectedVehicle = signal<VehicleDto | null>(null);
  selectedVehicleType = signal<VehicleTypeDto | null>(null);

  constructor(private dialog: MatDialog) {
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

    this.vehicleForm.patchValue({
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
    this.dialog.open(AdminVehiclesDialog).afterClosed().subscribe((vehicle) => {
      if (vehicle) {
        this.snackBar.open("Vehicle added successfully", '', {horizontalPosition: "right", duration : 3000});
        this.vehicleCreate$.next(vehicle);
      } else {
        this.snackBar.open("Vehicle add failed",'', {horizontalPosition: "right", duration : 3000});
      }
    });
  }

  protected saveVehicle() {
    const vehicle = this.selectedVehicle();
    const vehicleType = this.selectedVehicleType();

    if (!vehicle || !vehicleType) {
      return;
    }

    vehicle.model = this.vehicleForm.get("model")?.value!;
    vehicle.year = this.vehicleForm.get("year")?.value!;
    vehicle.licensePlate = this.vehicleForm.get("licensePlate")?.value!;
    vehicle.numberOfSeats = this.vehicleForm.get("numberOfSeats")?.value!;
    vehicle.babySeat = this.vehicleForm.get("babySeat")?.value!;
    vehicle.petFriendly = this.vehicleForm.get("petFriendly")?.value!;

    this.apiService.vehicleApi.updateVehicle(vehicle.id, vehicle).pipe(
      map(response => response.data),
      catchError(error => {
        this.snackBar.open(error, '', {horizontalPosition: "right", duration : 3000});
        return of(null);
      }),
    ).subscribe((vehicleType) => {
      if (vehicleType) {
        this.snackBar.open("Vehicle updated successfully", '', {horizontalPosition: "right", duration : 3000});
        this.vehicleUpdate$.next(vehicleType);
      } else {
        this.snackBar.open("Vehicle update failed", '', {horizontalPosition: "right", duration : 3000});
      }
    });


  }

}
