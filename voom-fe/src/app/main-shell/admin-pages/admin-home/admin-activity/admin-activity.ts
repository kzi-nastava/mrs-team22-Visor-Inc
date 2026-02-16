import { Component, inject, signal } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import ApiService from '../../../../shared/rest/api-service';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { ValueInputDate } from '../../../../shared/value-input/value-input-date/value-input-date';
import { RideHistoryDto } from '../../../../shared/rest/ride/ride.model';
import { MatDialog } from '@angular/material/dialog';
import { ActivityMap } from '../../../../shared/activity-map/activity-map';
import { combineLatest, map, startWith, switchMap } from 'rxjs';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { Dropdown } from '../../../../shared/dropdown/dropdown';

export const ROUTE_ADMIN_ACTIVITY = "activity";

@Component({
  selector: 'app-admin-activity',
  imports: [
    MatButton,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    ValueInputDate,
    ReactiveFormsModule,
    Dropdown
  ],
  templateUrl: './admin-activity.html',
  styleUrl: './admin-activity.css',
})
export class AdminActivity {

  protected values: { label: string; value: string }[] = [
    { label: 'Newest first', value: 'DATE_DESC' },
    { label: 'Oldest first', value: 'DATE_ASC' },

    { label: 'Price: low → high', value: 'PRICE_ASC' },
    { label: 'Price: high → low', value: 'PRICE_DESC' },

    { label: 'Shortest distance', value: 'DISTANCE_ASC' },
    { label: 'Longest distance', value: 'DISTANCE_DESC' },

    { label: 'Status ascending', value: 'STATUS_ASC' },
    { label: 'Status descending', value: 'STATUS_DESC' }
  ];

  private apiService = inject(ApiService);

  selectedColumnName = signal< "DATE" | "PRICE" | "DISTANCE" | "STATUS">("DATE")
  sortDirection = signal<"ASC" | "DESC">("DESC");

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
      const columnName = this.selectedColumnName();

      return this.apiService.rideApi.getRides(startDateStr ?? null, endDateStr ?? null, columnName ?? null, sort).pipe(
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

  protected onSortChange($event: string | number) {
    switch ($event) {
      case 'DATE_DESC':
        this.selectedColumnName.set("DATE");
        this.sortDirection.set("DESC");
        break;

      case 'DATE_ASC':
        this.selectedColumnName.set("DATE");
        this.sortDirection.set("ASC");
        break;

      case 'PRICE_DESC':
        this.selectedColumnName.set("PRICE");
        this.sortDirection.set("DESC");
        break;

      case 'PRICE_ASC':
        this.selectedColumnName.set("PRICE");
        this.sortDirection.set("ASC");
        break;

      case 'DISTANCE_DESC':
        this.selectedColumnName.set("DISTANCE");
        this.sortDirection.set("DESC");
        break;

      case 'DISTANCE_ASC':
        this.selectedColumnName.set("DISTANCE");
        this.sortDirection.set("ASC");
        break;

      case 'STATUS_ASC':
        this.selectedColumnName.set("STATUS");
        this.sortDirection.set("DESC");
        break;

      case 'STATUS_DESC':
        this.selectedColumnName.set("STATUS");
        this.sortDirection.set("ASC");
        break;
    }
  }

  protected getParsedDate(scheduledTime: string | undefined) {
    if (!scheduledTime) {
      return '';
    }

    const date = new Date(scheduledTime);

    return date.toLocaleDateString('en-GB', {
      timeZone: 'Europe/Belgrade',
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
  }
}
