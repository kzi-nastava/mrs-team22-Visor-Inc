import {Component, inject, signal} from '@angular/core';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {MatDivider} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ValueInputString} from '../../../../shared/value-input/value-input-string/value-input-string';
import ApiService from '../../../../shared/rest/api-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {map} from 'rxjs';
import {toSignal} from '@angular/core/rxjs-interop';
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

  protected savePricing() {

  }

  vehicleTypes$ = this.apiService.vehicleTypeApi.getVehicleTypes().pipe(
    map(response => response.data),
  );

  vehicleTypes = toSignal(this.vehicleTypes$);

  selectedVehicleType = signal<VehicleTypeDto | null>(null);

  constructor(private dialog: MatDialog) {
  }

  protected selectVehicleType(vehicleType: VehicleTypeDto) {

    if (!vehicleType) {
      return;
    }

    this.priceForm.setValue({
      type: vehicleType.type,
      price: vehicleType.price
    });

    this.priceForm.get("type")?.disable();
    this.priceForm.get("price")?.enable();

    this.selectedVehicleType.set(vehicleType);
  }

  protected addVehicleType() {
    this.dialog.open(AdminPricingDialog).afterClosed().subscribe((vehicleType) => {

    })
  }

  protected savePrice() {

  }

  protected deleteVehicleType() {

  }
}
