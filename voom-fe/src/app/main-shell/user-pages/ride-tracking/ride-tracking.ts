import { Component, signal, ViewChild, OnInit } from '@angular/core';
import { Map } from '../../../shared/map/map';
import { DriverSimulationWsService } from '../../../shared/websocket/DriverSimulationWsService';
import ApiService from '../../../shared/rest/api-service';
import { ActivatedRoute } from '@angular/router';
import { OngoingRideDto } from '../../../shared/rest/home/home.model';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';

export const ROUTE_RIDE_TRACKING = 'ride/tracking';

@Component({
  selector: 'app-ride-tracking',
  imports: [Map, MatButtonModule, FormsModule],
  templateUrl: './ride-tracking.html',
  styleUrl: './ride-tracking.css',
})
export class RideTracking implements OnInit {
  @ViewChild(Map) map!: Map;

  private rideId!: number;
  private driverId!: number;
  private rendered = false;

  showReport = signal<boolean>(false);
  reported = signal<boolean>(false);
  rideFinished = signal<boolean>(false);
  reviewed = signal<boolean>(false);
  
  startAddress = signal<string>('');
  destinationAddress = signal<string>('');
  
  driverRating = signal<number>(0);
  carRating = signal<number>(0);
  reviewComment = signal<string>('');

  message = "";
  stars = [1, 2, 3, 4, 5];

  constructor(
    private ws: DriverSimulationWsService,
    private api: ApiService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initDrive();
  }

  toggleReport(): void {
    this.showReport.update(v => !v);
  }

  report(): void {
    this.api.rideApi
      .reportRide(this.rideId, { message: this.message })
      .subscribe(() => {
        this.reported.set(true);
        this.showReport.set(false);
      });
  }

  private initDrive(): void {
    this.api.rideApi.getRide(this.rideId).subscribe((res) => {
      const ride: OngoingRideDto | null = res.data;
      if (!ride) return;

      this.rideId = ride.rideId;
      this.driverId = ride.driverId;

      this.startAddress.set(ride.routePoints.find(p => p.type === 'PICKUP')?.address || '');
      this.destinationAddress.set(ride.routePoints.find(p => p.type === 'DROPOFF')?.address || '');

      if (!this.driverId) return;

      this.map.drawRouteFromAddresses(this.startAddress(), this.destinationAddress());
      this.startTrackingDriver(ride);
    });
  }

  private startTrackingDriver(ride: any): void {
    this.ws.connect(
      () => {},
      () => {},
      undefined,
      (pos) => {
        if (pos.driverId !== this.driverId) return;

        if (pos.finished) {
          console.log("WebSocket: Ride Finished!");
          this.rideFinished.set(true);
          return;
        }

        if (!this.rendered) {
          this.rendered = true;
          this.map.addSimulatedDriver({
            id: this.driverId,
            firstName: ride.driverName?.split(' ')[0] ?? '',
            lastName: ride.driverName?.split(' ')[1] ?? '',
            start: { lat: pos.lat, lng: pos.lng },
            status: ride.status as any,
          });
        } else {
          this.map.updateDriverPosition(this.driverId, pos.lat, pos.lng);
        }
      }
    );
  }

  setDriverRating(rating: number): void {
    this.driverRating.set(rating);
  }

  setCarRating(rating: number): void {
    this.carRating.set(rating);
  }

  submitReview() {
    this.api.rideApi.rateRide(this.rideId, {
      driverRating: this.driverRating(),
      vehicleRating: this.carRating(),
      comment: this.reviewComment(),
    }).subscribe(() => {
      this.reviewed.set(true);
    });
  }
}