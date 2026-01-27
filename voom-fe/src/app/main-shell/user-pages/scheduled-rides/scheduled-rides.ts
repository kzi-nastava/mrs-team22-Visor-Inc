import {Component, inject} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import ApiService from '../../../shared/rest/api-service';
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from '@angular/material/expansion';
import {MatIcon} from '@angular/material/icon';
import {MatButton} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';
import {AuthenticationService} from '../../../shared/service/authentication-service';
import {BehaviorSubject, catchError, filter, map, merge, of, scan, switchMap} from 'rxjs';
import {toSignal} from '@angular/core/rxjs-interop';
import {RideCancellationDto, RideHistoryDto} from '../../../shared/rest/ride/ride.model';

export const ROUTE_SCHEDULED_RIDES = "scheduled";

@Component({
  selector: 'app-scheduled-rides',
  imports: [
    ReactiveFormsModule,
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatIcon,
    MatButton
  ],
  templateUrl: './scheduled-rides.html',
  styleUrl: './scheduled-rides.css',
})
export class ScheduledRides {

  private apiService = inject(ApiService);
  private snackbar = inject(MatSnackBar);
  private authenticationService = inject(AuthenticationService);

  initialScheduledRides$ = this.authenticationService.activeUser$.pipe(
    switchMap(user => {
      if (!user) {
        return of([]);
      }

      return this.apiService.rideApi.getScheduledRides(user.id).pipe(
        map(response => response.data ?? []),
      );
    })
  );

  rideCancelled = new BehaviorSubject<RideHistoryDto | null>(null);

  scheduledRides$ = merge(
    this.initialScheduledRides$.pipe(
      map(rides => ({ type: 'init' as const, rides }))
    ),
    this.rideCancelled.asObservable().pipe(
      filter(ride => !!ride),
      map(ride => ({ type: 'cancel' as const, ride }))
    )
  ).pipe(
    scan((state: RideHistoryDto[], event) => {
      switch (event.type) {
        case 'init':
          return event.rides;
        case 'cancel':
          return [...state.filter(r => r.id !== event.ride.id), event.ride];
        default:
          return state;
      }
    }, [])
  );

  scheduledRides = toSignal(this.scheduledRides$);
  user = toSignal(this.authenticationService.activeUser$);

  protected getFormattedDate() {
    const date = new Date();
    return `Today - ${date.toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    })}`;
  }

  protected rideCancel(scheduledRide: RideHistoryDto) {
    const user = this.user();

    console.log(scheduledRide, user);

    if (!user) return;

    const dto: RideCancellationDto = {
      userId: user.id,
      message: ''
    }

    this.apiService.rideApi.cancelScheduledRide(scheduledRide.id, dto).pipe(
      map(response => response.data),
      catchError(err => of(null))
    ).subscribe((scheduledRide) => {
      if (scheduledRide) {
        this.snackbar.open("Scheduled ride cancelled successfully", '', { duration:3000, horizontalPosition:'right' });
      } else {
        this.snackbar.open("Scheduled ride cancel failed", '', { duration:3000, horizontalPosition:'right' });
      }
      this.rideCancelled.next(scheduledRide);
    });
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
}
