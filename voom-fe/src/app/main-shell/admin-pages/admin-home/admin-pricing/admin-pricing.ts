import {Component} from '@angular/core';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {MatDivider} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ValueInputString} from '../../../../shared/value-input/value-input-string/value-input-string';
import {ValueInputNumeric} from '../../../../shared/value-input/value-input-numeric/value-input-numeric';

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
  pricingGeneralForm = new FormGroup({
    type: new FormControl<string>('', [Validators.required, Validators.minLength(2), Validators.maxLength(255)]),
    price: new FormControl<number | null>(null, [Validators.required]),
  });

  protected savePricing() {

  }
}
