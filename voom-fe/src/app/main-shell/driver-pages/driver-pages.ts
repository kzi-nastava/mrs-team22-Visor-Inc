import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';

export const ROUTE_DRIVER_PAGES = "driver";

@Component({
  selector: 'app-driver-pages',
  imports: [
    RouterOutlet
  ],
  templateUrl: './driver-pages.html',
  styleUrl: './driver-pages.css',
})
export class DriverPages {

}
