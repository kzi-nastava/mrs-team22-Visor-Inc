import { Component } from '@angular/core';
// import { Header } from '../../core/layout/header/header';
import { RideHistoryTable } from './components/ride-history-table/ride-history-table';
import { ValueInputDate } from '../../shared/value-input/value-input-date/value-input-date';
import { MatButton } from '@angular/material/button';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Footer } from '../../core/layout/footer/footer';
import { Header } from '../../core/layout/header-kt1/header-kt1';
import { MatInput } from '@angular/material/input';

export const ROUTE_DRIVER_RIDE_HISTORY = 'driver/rideHistory';

@Component({
  selector: 'app-driver-ride-history',
  imports: [Header, RideHistoryTable, ValueInputDate, MatButton, ReactiveFormsModule, Footer],
  templateUrl: './driver-ride-history.html',
  styleUrl: './driver-ride-history.css',
})
export class DriverRideHistory {
  fromDate = new FormControl<Date | null>(null);
  toDate = new FormControl<Date | null>(null);

  appliedFromDate: Date | null = null;
  appliedToDate: Date | null = null;

  selected = 'date-desc';

  options = [
    { value: 'date-asc', label: 'Date (ASC)' },
    { value: 'date-desc', label: 'Date (DESC)' },
  ];

  onSortChange(event: any) {
    // this.rideDataSource.sort = null;
    // let data = [...this.rideDataSource.data];
    // console.log(data);
    // data.sort((a: any, b: any) => {
    //   const dateA = a.date.getTime();
    //   const dateB = b.date.getTime();
    //   return event.value === 'date-asc' ? dateA - dateB : dateB - dateA;
    // });
    // this.rideDataSource.data = data;
  }

  applyFilter() {
    const rawFromDate = this.fromDate.value;
    const rawToDate = this.toDate.value;

    // manually setting hours to make both ranges inclusive
    if (rawFromDate) {
      const start = new Date(rawFromDate);
      start.setHours(0, 0, 0, 0);
      this.appliedFromDate = start;
    } else {
      this.appliedFromDate = null;
    }

    if (rawToDate) {
      const end = new Date(rawToDate);
      end.setHours(23, 59, 59, 999);
      this.appliedToDate = end;
    } else {
      this.appliedToDate = null;
    }
  }
}
