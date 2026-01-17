import { Component, ChangeDetectionStrategy, Input } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';

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
  selector: 'app-ride-accordion',
  imports: [MatExpansionModule],
  templateUrl: './ride-accordion.html',
  styleUrl: './ride-accordion.css',
})
export class RideAccordion {
  @Input({ required: true }) ride!: Ride;
  get orderedPassengerName() {
    const ordered = this.ride.passengers.find((p) => p.orderedRide);
    return `${ordered?.name} ${ordered?.lastname}`;
  }
}
