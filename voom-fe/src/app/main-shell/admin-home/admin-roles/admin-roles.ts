import { Component } from '@angular/core';
import {MatCard} from '@angular/material/card';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {ValueInputString} from '../../../shared/value-input/value-input-string/value-input-string';

export const ROUTE_ADMIN_ROLES = "roles";

@Component({
  selector: 'app-admin-roles',
  imports: [
    MatCard,
    MatDrawer,
    MatDrawerContainer,
    MatDrawerContent,
    ValueInputString
  ],
  templateUrl: './admin-roles.html',
  styleUrl: './admin-roles.css',
})
export class AdminRoles {

}
