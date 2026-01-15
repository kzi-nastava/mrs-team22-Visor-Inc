import {Component} from '@angular/core';
import {MatDrawer, MatDrawerContainer, MatDrawerContent} from '@angular/material/sidenav';
import {ValueInputString} from '../../../shared/value-input/value-input-string/value-input-string';
import {MatCard} from '@angular/material/card';

export const ROUTE_ADMIN_USERS = "users";

@Component({
  selector: 'app-admin-users',
  imports: [
    MatDrawer,
    MatDrawerContainer,
    MatDrawerContent,
    ValueInputString,
    MatCard
  ],
  templateUrl: './admin-users.html',
  styleUrl: './admin-users.css',
})
export class AdminUsers {

}
