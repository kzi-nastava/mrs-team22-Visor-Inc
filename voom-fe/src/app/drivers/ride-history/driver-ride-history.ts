import { Component } from '@angular/core';
import { Header } from '../../core/layout/header/header';
import { RideHistoryTable } from './components/ride-history-table/ride-history-table';

export const ROUTE_DRIVER_RIDE_HISTORY = 'driver/rideHistory';

@Component({
  selector: 'app-driver-ride-history',
  imports: [Header, RideHistoryTable],
  templateUrl: './driver-ride-history.html',
  styleUrl: './driver-ride-history.css',
})
export class DriverRideHistory {}
