import { Component, OnInit, inject, signal, ViewChild } from '@angular/core';
import { ValueInputDate } from '../../../shared/value-input/value-input-date/value-input-date';
import { MatIconButton } from '@angular/material/button';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RideAccordion } from './ride-accordion/ride-accordion';
import { MatIcon } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import ApiService from '../../../shared/rest/api-service';
import { RideHistoryDto } from '../../../shared/rest/home/home.model';

export const ROUTE_DRIVER_RIDE_HISTORY = 'activity';

@Component({
  selector: 'app-driver-ride-history',
  standalone: true,
  imports: [
    CommonModule,
    ValueInputDate,
    MatIcon,
    ReactiveFormsModule,
    RideAccordion,
    MatIconButton,
  ],
  templateUrl: './driver-ride-history.html',
  styleUrl: './driver-ride-history.css',
})
export class DriverRideHistory implements OnInit {
  private api = inject(ApiService);

  fromDate = new FormControl<Date | null>(null);
  toDate = new FormControl<Date | null>(null);

  rides = signal<RideHistoryDto[]>([]);
  dates = signal<Date[]>([]);
  sortDirection = signal<'asc' | 'desc'>('desc');

  groupedRides = signal<Map<number, RideHistoryDto[]>>(new Map());

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(): void {
    const from = this.fromDate.value;
    const to = this.toDate.value;

    const fromCopy = from ? new Date(from) : null;
    const toCopy = to ? new Date(to) : null;

    if (fromCopy) fromCopy.setHours(0, 0, 0, 0);
    if (toCopy) toCopy.setHours(23, 59, 59, 999);

    this.api.rideApi.getDriverRideHistory(fromCopy, toCopy, this.sortDirection()).subscribe({
        next: (res) => {
            const backendRides = res.data || [];
            this.processAndGroupRides(backendRides);
        },
        error: (err) => {
            console.error('Failed to load ride history:', err);
        }
    });
}

  private processAndGroupRides(rides: RideHistoryDto[]): void {
    const groups = new Map<number, RideHistoryDto[]>();

    rides.forEach((ride) => {
      const date = new Date(ride.startedAt);
      date.setHours(0, 0, 0, 0);
      const timestamp = date.getTime();

      if (!groups.has(timestamp)) {
        groups.set(timestamp, []);
      }
      groups.get(timestamp)?.push(ride);
    });

    this.groupedRides.set(groups);
    this.updateSortedDates();
  }

  private updateSortedDates(): void {
    const sortedTimestamps = Array.from(this.groupedRides().keys()).sort((a, b) => {
      return this.sortDirection() === 'asc' ? a - b : b - a;
    });

    this.dates.set(sortedTimestamps.map((t) => new Date(t)));
  }

  applyFilter(): void {
    this.loadHistory();
  }


  sort(changeDirection: boolean = false): void {
    if (changeDirection) {
        this.sortDirection.update((val) => (val === 'asc' ? 'desc' : 'asc'));
        this.loadHistory(); 
    } else {
        this.updateSortedDates();
    }
}

  getRidesForDate(date: Date): RideHistoryDto[] {
    return this.groupedRides().get(date.getTime()) || [];
  }
}