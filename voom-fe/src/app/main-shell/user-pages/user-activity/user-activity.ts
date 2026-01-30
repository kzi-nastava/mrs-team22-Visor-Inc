import {Component, inject, signal} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import ApiService from '../../../shared/rest/api-service';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {ValueInputDate} from '../../../shared/value-input/value-input-date/value-input-date';
import {AuthenticationService} from '../../../shared/service/authentication-service';
import {filter, map, switchMap} from 'rxjs';
import {toSignal} from '@angular/core/rxjs-interop';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from '@angular/material/expansion';
import {MatButton} from '@angular/material/button';
import {RideHistoryDto} from '../../../shared/rest/ride/ride.model';
import {MatDialog} from '@angular/material/dialog';
import {ActivityMap} from '../../../shared/activity-map/activity-map';

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

  sortDirection = signal<'asc' | 'desc'>('desc');

  fromDate = new FormControl<Date | null>(null);
  toDate = new FormControl<Date | null>(null);

  userRideHistory$ = this.authenticationService.activeUser$.pipe(
    filter(user => !!user),
    switchMap(user => {

      const from = this.fromDate.value;
      const to = this.toDate.value;
      const sortDirection = this.sortDirection();

      return this.apiService.rideApi.getUserRideHistory(user.id, from, to, sortDirection).pipe(
        map((response) => response.data),
      );
    }),
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
}
