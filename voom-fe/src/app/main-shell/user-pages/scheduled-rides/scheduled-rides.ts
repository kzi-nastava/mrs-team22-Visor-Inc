import {Component, inject} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import ApiService from '../../../shared/rest/api-service';
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from '@angular/material/expansion';
import {ValueInputTime} from '../../../shared/value-input/value-input-time/value-input-time';
import {MatIcon} from '@angular/material/icon';
import {MatButton} from '@angular/material/button';
import {MatSnackBar} from '@angular/material/snack-bar';
import {AuthenticationService} from '../../../shared/service/authentication-service';
import {catchError, filter, map, of, switchMap} from 'rxjs';
import {toSignal} from '@angular/core/rxjs-interop';
import {RideRequestResponseDto} from '../../../shared/rest/ride/ride.model';

export const ROUTE_SCHEDULED_RIDES = "scheduled";

@Component({
  selector: 'app-scheduled-rides',
  imports: [
    ReactiveFormsModule,
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    ValueInputTime,
    MatIcon,
    MatButton
  ],
  templateUrl: './scheduled-rides.html',
  styleUrl: './scheduled-rides.css',
})
export class ScheduledRides {

  private apiService = inject(ApiService);
  private matSnackBar = inject(MatSnackBar);
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

  scheduledRides = toSignal(this.initialScheduledRides$);

  protected getFormattedDate() {
    const date = new Date();
    return `Today - ${date.toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    })}`;
  }

  protected rideCancel(scheduledRide: RideRequestResponseDto) {

  }
}
