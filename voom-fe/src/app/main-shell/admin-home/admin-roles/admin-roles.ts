import { Component } from '@angular/core';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {ValueInputString} from '../../../shared/value-input/value-input-string/value-input-string';
import {MatDivider} from '@angular/material/list';
import {MatIcon} from '@angular/material/icon';

export const ROUTE_ADMIN_ROLES = "roles";

@Component({
  selector: 'app-admin-roles',
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
  templateUrl: './admin-roles.html',
  styleUrl: './admin-roles.css',
})
export class AdminRoles {

}
