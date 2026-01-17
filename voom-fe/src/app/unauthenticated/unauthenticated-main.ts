import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';

export const ROUTE_UNAUTHENTICATED_MAIN = "";

@Component({
  selector: 'app-unauthenticated-main',
  imports: [
    RouterOutlet
  ],
  templateUrl: './unauthenticated-main.html',
  styleUrl: './unauthenticated-main.css',
})
export class UnauthenticatedMain {

}
