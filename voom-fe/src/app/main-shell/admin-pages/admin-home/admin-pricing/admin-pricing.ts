import {Component, inject, signal} from '@angular/core';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {MatDivider} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ValueInputString} from '../../../../shared/value-input/value-input-string/value-input-string';
import ApiService from '../../../../shared/rest/api-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import { BehaviorSubject, catchError, filter, map, merge, Observable, of, scan } from 'rxjs';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import {VehicleTypeDto} from '../../../../shared/rest/vehicle/vehicle-type.model';
import {ValueInputNumeric} from '../../../../shared/value-input/value-input-numeric/value-input-numeric';
import {MatDialog} from '@angular/material/dialog';
import {AdminPricingDialog} from './admin-pricing-dialog/admin-pricing-dialog';

export const ROUTE_ADMIN_PRICING = "pricing"

@Component({
  selector: 'app-admin-pricing',
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
    FormsModule,
    ReactiveFormsModule,
    ValueInputNumeric
  ],
  templateUrl: './admin-pricing.html',
  styleUrl: './admin-pricing.css',
})
export class AdminPricing {

  priceForm = new FormGroup({
    type: new FormControl<string>({value: '', disabled: true}, [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    price: new FormControl<number | null>({value: null, disabled: false}, [Validators.required]),
  });

  private apiService = inject(ApiService);
  private snackBar = inject(MatSnackBar);

  initialVehicleTypes$ = this.apiService.vehicleTypeApi.getVehicleTypes().pipe(
    map(response => response.data),
  );

  vehicleTypeCreate$ = new BehaviorSubject<VehicleTypeDto | null>(null);
  vehicleTypeUpdate$ = new BehaviorSubject<VehicleTypeDto | null>(null);

  vehicleTypes$= merge(
    this.initialVehicleTypes$.pipe(filter(vehicleType => !!vehicleType), map(response => { return {type:'initial', value: response} })),
    this.vehicleTypeCreate$.asObservable().pipe(takeUntilDestroyed(), filter(vehicleType => !!vehicleType), map(response => { return {type:'create', value: [response]} })),
    this.vehicleTypeUpdate$.asObservable().pipe(takeUntilDestroyed(), filter(vehicleType => !!vehicleType), map(response => { return {type:'update', value: [response]} })),
  ).pipe(
    takeUntilDestroyed(),
    scan((acc, obj) => {
      switch (obj.type) {
        case 'initial':
          return obj.value;
        case 'create':
          return [...acc, ...obj.value];
        case 'update':
          return [...acc.filter(vehicleType => vehicleType.id !== obj.value[0].id), obj.value[0]];
        default:
          return [];
      }
    }, [] as VehicleTypeDto[]),
  )

  vehicleTypes = toSignal(this.vehicleTypes$);

  selectedVehicleType = signal<VehicleTypeDto | null>(null);

  constructor(private dialog: MatDialog) {
  }

  protected selectVehicleType(vehicleType: VehicleTypeDto) {

    if (!vehicleType) {
      return;
    }

    this.priceForm.patchValue({
      type: vehicleType.type,
      price: vehicleType.price
    });

    this.priceForm.get("type")?.disable();
    this.priceForm.get("price")?.enable();

    this.selectedVehicleType.set(vehicleType);
  }

  protected addVehicleType() {
    this.dialog.open(AdminPricingDialog).afterClosed().subscribe((vehicleType) => {
      if (vehicleType) {
        this.snackBar.open("Vehicle type added successfully", '', {horizontalPosition: "right", duration : 3000});
        this.vehicleTypeCreate$.next(vehicleType);
      } else {
        this.snackBar.open("Vehicle type add failed", '', {horizontalPosition: "right", duration : 3000});
      }
    });
  }

  protected savePrice() {
    const vehicleType = this.selectedVehicleType();
    const price = this.priceForm.get("price")?.value;

    if (!vehicleType || !price) {
      return;
    }

    vehicleType.price = price;

    this.apiService.vehicleTypeApi.updateVehicleType(vehicleType.id, vehicleType).pipe(
      map(response => response.data),
      catchError(error => {
        this.snackBar.open(error, '', {horizontalPosition: "right", duration : 3000});
        return of(null);
      }),
    ).subscribe((vehicleType) => {
      if (vehicleType) {
        this.snackBar.open("Vehicle type updated successfully", '', {horizontalPosition: "right", duration : 3000});
        this.vehicleTypeUpdate$.next(vehicleType);
      } else {
        this.snackBar.open("Vehicle type update failed", '', {horizontalPosition: "right", duration : 3000});
      }
    });
  }
}
