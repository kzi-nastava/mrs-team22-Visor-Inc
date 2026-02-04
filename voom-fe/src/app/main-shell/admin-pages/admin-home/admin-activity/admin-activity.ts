import {Component, inject, signal} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {MatButton} from '@angular/material/button';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from '@angular/material/expansion';
import ApiService from '../../../../shared/rest/api-service';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {ValueInputDate} from '../../../../shared/value-input/value-input-date/value-input-date';
import {RideHistoryDto} from '../../../../shared/rest/ride/ride.model';
import {MatDialog} from '@angular/material/dialog';
import {ActivityMap} from '../../../../shared/activity-map/activity-map';
import {combineLatest, map, startWith, switchMap} from 'rxjs';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';

export const ROUTE_ADMIN_ACTIVITY = "activity";

@Component({
  selector: 'app-admin-activity',
  imports: [
    MatIcon,
    MatButton,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    ValueInputDate,
    ReactiveFormsModule
  ],
  templateUrl: './admin-activity.html',
  styleUrl: './admin-activity.css',
})
export class AdminActivity {

  private apiService = inject(ApiService);

  sortDirection = signal<'ASC' | 'DESC'>('DESC');

  fromDate = new FormControl<Date | null>(null);
  toDate = new FormControl<Date | null>(null);

  private userRideHistory$ = combineLatest([
    this.fromDate.valueChanges.pipe(startWith(null)),
    this.toDate.valueChanges.pipe(startWith(null)),
    toObservable(this.sortDirection),
  ]).pipe(
    switchMap(([start, end, sort]) => {
      const startDateStr = start?.toISOString();
      const endDateStr = end?.toISOString();

      return this.apiService.rideApi.getRides(startDateStr ?? null, endDateStr ?? null, sort).pipe(
        map(response => response.data ?? []),
      );
    })
  );

  rideHistory = toSignal(this.userRideHistory$);

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
