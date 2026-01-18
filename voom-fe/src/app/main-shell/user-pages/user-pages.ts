import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';

export const ROUTE_USER_PAGES = "user";

@Component({
  selector: 'app-user-pages',
  imports: [
    RouterOutlet
  ],
  templateUrl: './user-pages.html',
  styleUrl: './user-pages.css',
})
export class UserPages {

}
