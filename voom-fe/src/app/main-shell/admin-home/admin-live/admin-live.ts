import { Component } from '@angular/core';
import {Map} from '../../../shared/map/map';

export const ROUTE_ADMIN_LIVE = "live"

@Component({
  selector: 'app-admin-live',
  imports: [
    Map
  ],
  templateUrl: './admin-live.html',
  styleUrl: './admin-live.css',
})
export class AdminLive {

}
