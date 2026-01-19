import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';

export const ROUTE_ADMIN_PAGES = "admin";

@Component({
  selector: 'app-admin-pages',
  imports: [
    RouterOutlet
  ],
  templateUrl: './admin-pages.html',
  styleUrl: './admin-pages.css',
})
export class AdminPages {

}
