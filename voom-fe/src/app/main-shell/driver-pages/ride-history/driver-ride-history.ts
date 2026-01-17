import {Component} from '@angular/core';
import {ValueInputDate} from '../../../shared/value-input/value-input-date/value-input-date';
import {MatIconButton} from '@angular/material/button';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {RideAccordion} from './ride-accordion/ride-accordion';
import {MatIcon} from '@angular/material/icon';

export const ROUTE_DRIVER_RIDE_HISTORY = 'activity';

type TimeString = `${number}:${number}` | `${number}:''`;

export interface Passenger {
  name: string;
  lastname: string;
  orderedRide: boolean;
}

export interface Ride {
  date: Date;
  startTime: TimeString;
  endTime: TimeString | '';
  source: string;
  destination: string;
  status: string;
  price: number;
  panic: boolean;
  passengers: Passenger[];
}

@Component({
  selector: 'app-driver-ride-history',
  imports: [
    ValueInputDate,
    MatIcon,
    ReactiveFormsModule,
    RideAccordion,
    MatIconButton,
  ],
  templateUrl: './driver-ride-history.html',
  styleUrl: './driver-ride-history.css',
})
export class DriverRideHistory {
  fromDate = new FormControl<Date | null>(null);
  toDate = new FormControl<Date | null>(null);

  appliedFromDate: Date | null = null;
  appliedToDate: Date | null = null;

  RIDE_DATA: Ride[] = [
    {
      date: new Date('2025-03-01'),
      startTime: '08:15',
      endTime: '08:45',
      source: 'Bulevar Oslobođenja 45',
      destination: 'Železnička stanica Novi Sad',
      status: 'Completed',
      price: 850,
      passengers: [
        { name: 'Marko', lastname: 'Petrović', orderedRide: true },
        { name: 'Ivan', lastname: 'Jovanović', orderedRide: false },
      ],
      panic: false,
    },
    {
      date: new Date('2025-03-01'),
      startTime: '10:30',
      endTime: '11:05',
      source: 'Limanski park',
      destination: 'Futoška pijaca',
      status: 'Completed',
      price: 720,
      passengers: [{ name: 'Ana', lastname: 'Nikolić', orderedRide: true }],
      panic: false,
    },
    {
      date: new Date('2025-03-02'),
      startTime: '09:00',
      endTime: '09:40',
      source: 'Detelinara',
      destination: 'FTN',
      status: 'Completed',
      price: 900,
      passengers: [
        { name: 'Stefan', lastname: 'Ilić', orderedRide: true },
        { name: 'Maja', lastname: 'Kovačević', orderedRide: false },
        { name: 'Luka', lastname: 'Marić', orderedRide: false },
      ],
      panic: false,
    },
    {
      date: new Date('2025-03-03'),
      startTime: '14:10',
      endTime: '',
      source: 'Spens',
      destination: 'Centar',
      status: 'Cancelled by you',
      price: 0,
      passengers: [{ name: 'Jelena', lastname: 'Popović', orderedRide: true }],
      panic: false,
    },
    {
      date: new Date('2025-03-03'),
      startTime: '16:20',
      endTime: '16:55',
      source: 'Novo Naselje',
      destination: 'Promenada',
      status: 'Completed',
      price: 780,
      passengers: [
        { name: 'Nemanja', lastname: 'Stojanović', orderedRide: true },
        { name: 'Filip', lastname: 'Đorđević', orderedRide: false },
      ],
      panic: false,
    },
    {
      date: new Date('2025-03-04'),
      startTime: '07:40',
      endTime: '08:10',
      source: 'Veternik',
      destination: 'Centar',
      status: 'Completed',
      price: 950,
      passengers: [{ name: 'Milica', lastname: 'Ristić', orderedRide: true }],
      panic: false,
    },
    {
      date: new Date('2025-03-04'),
      startTime: '12:00',
      endTime: '12:35',
      source: 'Telep',
      destination: 'Klinički centar',
      status: 'Completed',
      price: 820,
      passengers: [
        { name: 'Ognjen', lastname: 'Savić', orderedRide: true },
        { name: 'Petar', lastname: 'Milošević', orderedRide: false },
      ],
      panic: true,
    },
    {
      date: new Date('2025-03-05'),
      startTime: '18:30',
      endTime: '19:05',
      source: 'Centar',
      destination: 'Petrovaradin',
      status: 'Completed',
      price: 880,
      passengers: [{ name: 'Sara', lastname: 'Lazić', orderedRide: true }],
      panic: false,
    },
    {
      date: new Date('2025-03-06'),
      startTime: '20:10',
      endTime: '',
      source: 'Promenada',
      destination: 'Limanski park',
      status: `Cancelled by ${'Vuk'}`,
      price: 0,
      passengers: [{ name: 'Vuk', lastname: 'Obradović', orderedRide: true }],
      panic: false,
    },
    {
      date: new Date('2025-03-06'),
      startTime: '22:15',
      endTime: '22:50',
      source: 'Petrovaradin',
      destination: 'Novo Naselje',
      status: 'Completed',
      price: 920,
      passengers: [
        { name: 'Teodora', lastname: 'Pavlović', orderedRide: true },
        { name: 'Andrej', lastname: 'Mitrović', orderedRide: false },
      ],
      panic: false,
    },
  ];

  uniqueDates(): Date[] {
    const unique: number[] = [];
    const uniqueDates: Date[] = [];
    for (let ride of this.RIDE_DATA) {
      if (!unique.includes(ride.date.getDate())) {
        unique.push(ride.date.getDate());
        uniqueDates.push(ride.date);
      }
    }
    return uniqueDates;
  }

  dates: Date[];

  constructor() {
    this.dates = this.uniqueDates();
    this.sort();
  }

  sortDirection: 'asc' | 'desc' = 'desc';

  sort(changeDirection: boolean = false) {
    if (changeDirection) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    }

    this.dates.sort((a, b) =>
      this.sortDirection === 'asc' ? a.getTime() - b.getTime() : b.getTime() - a.getTime()
    );
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

    this.dates = this.uniqueDates().filter((date) => {
      if (this.appliedFromDate && date.getTime() < this.appliedFromDate.getTime()) {
        return false;
      }
      if (this.appliedToDate && date.getTime() > this.appliedToDate.getTime()) {
        return false;
      }
      return true;
    });

    this.sort();
  }
}
