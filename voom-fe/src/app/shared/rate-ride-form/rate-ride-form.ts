import { Component, inject, Inject, signal } from '@angular/core';
import ApiService from '../rest/api-service';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-rate-ride-form',
  imports: [FormsModule],
  templateUrl: './rate-ride-form.html',
  styleUrl: './rate-ride-form.css',
})
export class RateRideForm {

  private snackBar = inject(MatSnackBar);

  rideId = signal<number | null>(null);

  reviewed = signal<boolean>(false);

  driverRating = signal<number>(0);
  carRating = signal<number>(0);
  reviewComment = signal<string>('');


  message = "";
  stars = [1, 2, 3, 4, 5];

  constructor(
    private api: ApiService,
    private dialogRef: MatDialogRef<RateRideForm>,
    @Inject(MAT_DIALOG_DATA) public data: { rideId: number }
  ) {
    if (this.data && this.data.rideId) {
      this.rideId.set(this.data.rideId);
    }
  }

    setDriverRating(rating: number): void {
    this.driverRating.set(rating);
  }

  setCarRating(rating: number): void {
    this.carRating.set(rating);
  }

  submitReview() {
    const rideId = this.rideId();

    if (!rideId) return;

    if (this.driverRating() === 0 || this.carRating() === 0) {
      this.snackBar.open('Please provide ratings for both driver and vehicle before submitting your review.', 'Close', { 
          duration: 2000,
          panelClass: ['error-snackbar']
        });
      return;
    }

    this.api.rideApi.rateRide(rideId, {
      driverRating: this.driverRating(),
      vehicleRating: this.carRating(),
      comment: this.reviewComment(),
    }).subscribe(() => {
      this.reviewed.set(true);
      this.snackBar.open('Review submitted successfully!', 'Close', { 
          duration: 100000,
          panelClass: ['success-snackbar']
        });
      this.dialogRef.close(true);
    });
  }
}
