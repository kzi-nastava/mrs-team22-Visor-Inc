import { Component, inject, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import ApiService from '../../../shared/rest/api-service';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ValueInputDate } from '../../../shared/value-input/value-input-date/value-input-date';
import { AuthenticationService } from '../../../shared/service/authentication-service';
import { combineLatest, map, of, startWith, switchMap } from 'rxjs';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatButton } from '@angular/material/button';
import { RideHistoryDto } from '../../../shared/rest/ride/ride.model';
import { MatDialog } from '@angular/material/dialog';
import { ActivityMap } from '../../../shared/activity-map/activity-map';
import { RateRideForm } from '../../../shared/rate-ride-form/rate-ride-form';

export const ROUTE_USER_ACTIVITY = "activity";

@Component({
  selector: 'app-user-activity',
  imports: [
    MatIcon,
    ValueInputDate,
    ReactiveFormsModule,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatButton
  ],
  templateUrl: './user-activity.html',
  styleUrl: './user-activity.css',
})
export class UserActivity {

  private apiService = inject(ApiService);
  private authenticationService = inject(AuthenticationService);

  sortDirection = signal<'ASC' | 'DESC'>('DESC');

  fromDate = new FormControl<Date | null>(null);
  toDate = new FormControl<Date | null>(null);

  locallyRatedRideIds = signal<number[]>([]);

  userRideHistory$ = combineLatest([
    this.authenticationService.activeUser$,
    this.fromDate.valueChanges.pipe(startWith(this.fromDate.value)),
    this.toDate.valueChanges.pipe(startWith(this.toDate.value)),
    toObservable(this.sortDirection)
  ]).pipe(
    switchMap(([user, from, to, sort]) => {
      if (!user) return of([]);

      const startStr = from instanceof Date ? from.toISOString() : from;
      const endStr = to instanceof Date ? to.toISOString() : to;

      return this.apiService.rideApi.getUserRideHistory(user.id, startStr ?? null, endStr ?? null, sort).pipe(
        map(response => response.data ?? [])
      );
    })
  );

  userRideHistory = toSignal(this.userRideHistory$);

  constructor(private dialog: MatDialog) {
  }

  protected getParsedTime(scheduledTime: string | undefined) {
    if (!scheduledTime) {
      return '';
    }

    const date = new Date(scheduledTime);
    return date.toLocaleTimeString('en-GB', {
      timeZone: 'Europe/Belgrade',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  protected openMap(ride: RideHistoryDto) {
    this.dialog.open(ActivityMap, { data: ride });
  }

  protected openRateRide(ride: RideHistoryDto) {

    const dialogRef = this.dialog.open(RateRideForm, {
      data: { rideId: ride.id },
    });

    dialogRef.afterClosed().subscribe(result => {
    if (result === true) {
      this.locallyRatedRideIds.update(ids => [...ids, ride.id]);
    }
  });
  }

  protected canRate(ride: RideHistoryDto): boolean {
    const currentUser = this.authenticationService.currentUserValue;

    if (this.locallyRatedRideIds().includes(ride.id)) {
      return false;
    }

    if (!currentUser || !ride.finishedAt || ride.status !== 'FINISHED') {
      return false;
    }

    const alreadyRated = ride.ratings.some(
      (rating) => rating.rater.email === currentUser.email
    );

    const finishedDate = new Date(ride.finishedAt).getTime();
    const threeDaysInMs = 3 * 24 * 60 * 60 * 1000;
    const isTooOld = Date.now() - finishedDate > threeDaysInMs;

    return !alreadyRated && !isTooOld;
  }
}
