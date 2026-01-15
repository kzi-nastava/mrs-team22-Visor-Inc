import { Component } from '@angular/core';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {ValueInputString} from '../../../shared/value-input/value-input-string/value-input-string';
import {MatDivider} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';

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
    MatIcon
  ],
  templateUrl: './admin-pricing.html',
  styleUrl: './admin-pricing.css',
})
export class AdminPricing {

}
