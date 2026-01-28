import {Component, inject, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogModule, MatDialogRef} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {MatDivider} from '@angular/material/list';
import {ValueInputString} from '../../../shared/value-input/value-input-string/value-input-string';
import {FormControl, ReactiveFormsModule, Validators} from '@angular/forms';
import ApiService from '../../../shared/rest/api-service';
import {AuthenticationService} from '../../../shared/service/authentication-service';
import {toSignal} from '@angular/core/rxjs-interop';
import {catchError, map, of} from 'rxjs';
import {RideCancellationDto, RideRoutePointDto} from '../../../shared/rest/ride/ride.model';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ActiveRideDto} from '../../user-pages/user-home/home.api';
import {point} from 'leaflet';

export type ArrivalDialogResult = 'START' | 'CANCEL';

@Component({
  selector: 'app-arrival-dialog',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatDivider,
    ValueInputString,
    ReactiveFormsModule,
  ],
  templateUrl: './arrival-dialog.html',
  styleUrl: './arrival-dialog.css',
})
export class ArrivalDialog {

  cancelFormControl = new FormControl<string>('', Validators.required);

  private apiService = inject(ApiService);
  private authenticationService = inject(AuthenticationService);
  private snackBar = inject(MatSnackBar);

  user = toSignal(this.authenticationService.activeUser$);

  constructor(
    private dialogRef: MatDialogRef<ArrivalDialog, ArrivalDialogResult>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      pickupAddress: string
      activeRide: ActiveRideDto
    },
  ) {}

  cancel() {
    const value = this.cancelFormControl.value;
    const user = this.user();

    if (!value || !user) {
      return;
    }

    const dto: RideCancellationDto = {
      userId: user.id,
      message: value,
    }

    this.apiService.rideApi.cancelRide(this.data.activeRide.rideId, dto).pipe(
      map(result => result.data),
      catchError(error => {
        this.snackBar.open(error, '', { duration: 3000, horizontalPosition: 'right'});
        return of(null);
      }),
    ).subscribe((ride) => {
      this.dialogRef.close('CANCEL');
    });
  }

  start() {
    const dto = {
      routePoints: this.data.activeRide.routePoints.map((p) => ({
        lat: p.lat,
        lng: p.lng,
        orderIndex: p.orderIndex ?? 0,
        type: p.type,
        address: p.address,
      } as RideRoutePointDto)),
    };

    this.apiService.rideApi.startRide(this.data.activeRide.rideId, dto).pipe(
      map(response => response.data),
      catchError(error => {
        this.snackBar.open(error, '', { duration: 3000, horizontalPosition: 'right'});
        return of(null);
      }),
      ).subscribe(() => {
        this.snackBar.open('Ride started', 'OK', { duration: 3000 });
        this.dialogRef.close('START');
      },
    );

  }

  protected readonly point = point;
}
