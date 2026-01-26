import { Component, ChangeDetectionStrategy, Input } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { RideHistoryDto } from '../../../../shared/rest/ride/ride.model';

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
  @Input({ required: true }) ride!: RideHistoryDto;
  get orderedPassengerName() {
    const ordered = this.ride.rideRequest.creator.person;
    return `${ordered?.firstName} ${ordered?.lastName}`;
  }

  getTime() {
    const start = this.ride.startedAt ? this.ride.startedAt.split('T')[1].substring(0, 5) : '';
    const end = this.ride.finishedAt ? this.ride.finishedAt.split('T')[1].substring(0, 5) : '';
    return `${start} - ${end}`;
  }
}
