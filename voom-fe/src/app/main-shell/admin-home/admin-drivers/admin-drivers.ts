import { Component } from '@angular/core';
import {MatCard} from '@angular/material/card';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {ValueInputString} from '../../../shared/value-input/value-input-string/value-input-string';

export const ROUTE_ADMIN_DRIVERS = "drivers";

@Component({
  selector: 'app-admin-drivers',
  imports: [
    MatCard,
    MatDrawer,
    MatDrawerContainer,
    MatDrawerContent,
    ValueInputString
  ],
  templateUrl: './admin-drivers.html',
  styleUrl: './admin-drivers.css',
})
export class AdminDrivers {

}
