import {Component, Inject, inject, ViewChild} from '@angular/core';
import ApiService from '../rest/api-service';
import {Map} from '../map/map';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {RideHistoryDto, ScheduleType} from '../rest/ride/ride.model';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {Dropdown} from '../dropdown/dropdown';
import {MatButton} from '@angular/material/button';
import {RideRequestDto} from '../../main-shell/user-pages/user-home/home.api';
import {MatSnackBar} from '@angular/material/snack-bar';
import {map} from 'rxjs';

@Component({
  selector: 'app-activity-map',
  imports: [
    Map,
    ReactiveFormsModule,
    Dropdown,
    MatButton
  ],
  templateUrl: './activity-map.html',
  styleUrl: './activity-map.css',
})
export class ActivityMap {
  apiService = inject(ApiService);
  snackBar = inject(MatSnackBar);

  scheduledTime = new FormControl<string | null>(null);

  protected timeOptions = [
    { label: 'Now', value: 'NOW' },
    { label: 'Later', value: 'LATER' },
  ];

  selectedTime: ScheduleType = 'NOW';

  constructor(private dialogRef: MatDialogRef<ActivityMap>, @Inject(MAT_DIALOG_DATA) public data: RideHistoryDto) {
  }

  protected transformRoutePoints() {
    return this.data.rideRoute.routePoints.map(routePoint => {
      return {
        lat: routePoint.latitude,
        lng: routePoint.longitude,
        type: routePoint.pointType,
        orderIndex: routePoint.orderIndex,
      }
    });
  }

  protected getParsedTime(time: Date) {
    if (!time) {
      return '';
    }

    const date = new Date(time);
    return date.toLocaleTimeString('en-GB', {
      timeZone: 'Europe/Belgrade',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  get isLaterSelected(): boolean {
    return this.selectedTime === 'LATER';
  }

  get minTime(): string {
    const now = new Date();
    return now.toTimeString().slice(0, 5);
  }

  get maxTime(): string {
    const max = new Date();
    max.setHours(max.getHours() + 5);
    return max.toTimeString().slice(0, 5);
  }

  isScheduledTimeValid(): boolean {
    const value = this.scheduledTime.value;
    if (!value) return false;

    const now = new Date();
    now.setSeconds(0, 0);

    const selected = new Date();
    const [h, m] = value.split(':').map(Number);
    selected.setHours(h, m, 0, 0);

    if (selected < now) {
      selected.setDate(selected.getDate() + 1);
    }

    const diffMinutes = (selected.getTime() - now.getTime()) / 60000;

    return diffMinutes >= 0 && diffMinutes <= 300;
  }

  private buildScheduledDate(): string {
    const [h, m] = this.scheduledTime!.value!.split(':').map(Number);
    const date = new Date();
    date.setHours(h, m, 0, 0);
    return new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString();
  }

  confirmRide() {
    const schedule: { type: ScheduleType; startAt: string } =
      this.selectedTime === 'LATER'
        ? { type: 'LATER', startAt: this.buildScheduledDate() }
        : { type: 'NOW', startAt: new Date().toISOString() };

    const payload: RideRequestDto = {
      route: {
        points: this.data.rideRoute.routePoints.map((p) => ({
          lat: p.latitude,
          lng: p.longitude,
          orderIndex: p.orderIndex,
          type: p.pointType,
          address: p.address,
        })),
      },
      schedule,
      vehicleTypeId: this.data.rideRequest.vehicleType.id,
      preferences: {
        pets: this.data.rideRequest.petTransport,
        baby: this.data.rideRequest.babyTransport,
      },
      linkedPassengers: this.data.rideRequest.linkedPassengerEmails,
      freeDriversSnapshot: [
        {
          driverId: this.data.driver.id,
          lat: this.data.rideRoute.routePoints[0].latitude,
          lng: this.data.rideRoute.routePoints[0].longitude,
        }
      ]
    };

    this.apiService.rideApi.createRideRequest(payload).pipe(
      map(response => response.data),
    ).subscribe({
      next: (res) => {
        if (!res) {
          return;
        }
        if (res.status === 'ACCEPTED' && res.driver) {
          this.snackBar.open(
            `Ride accepted. Price: ${res.price}, Driver: ${res.driver?.firstName} ${res.driver?.lastName}`,
            'Close',
            { duration: 4000 },
          );
        } else {
          this.snackBar.open('No drivers available. Ride rejected.', 'Close', { duration: 4000 });
        }
      },
      error: () => {
        this.snackBar.open('Failed to create ride request', 'Close', { duration: 4000 });
      },
    });
  }
}
