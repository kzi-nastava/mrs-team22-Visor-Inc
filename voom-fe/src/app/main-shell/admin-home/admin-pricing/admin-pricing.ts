import { Component } from '@angular/core';
import {MatCard} from '@angular/material/card';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {ValueInputString} from '../../../shared/value-input/value-input-string/value-input-string';

export const ROUTE_ADMIN_PRICING = "pricing"

@Component({
  selector: 'app-admin-pricing',
  imports: [
    MatCard,
    MatDrawer,
    MatDrawerContainer,
    MatDrawerContent,
    ValueInputString
  ],
  templateUrl: './admin-pricing.html',
  styleUrl: './admin-pricing.css',
})
export class AdminPricing {

}
