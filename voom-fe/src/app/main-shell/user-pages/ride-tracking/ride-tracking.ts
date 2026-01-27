import {Component, inject, OnInit, signal, ViewChild} from '@angular/core';
import {Map} from '../../../shared/map/map';
import {DriverSimulationWsService} from '../../../shared/websocket/DriverSimulationWsService';
import {ActivatedRoute, Router} from '@angular/router';
import {MatButtonModule} from '@angular/material/button';
import {FormsModule} from '@angular/forms';
import {ActiveRideDto} from '../home/home.api';
import {ROUTE_USER_HOME, RoutePoint} from '../home/user-home';
import ApiService from '../../../shared/rest/api-service';
import {AuthenticationService} from '../../../shared/service/authentication-service';
import {toSignal} from '@angular/core/rxjs-interop';
import {map} from 'rxjs';
import {ROUTE_HOME} from '../../../unauthenticated/home/home';

export const ROUTE_RIDE_TRACKING = 'ride/tracking';

@Component({
  selector: 'app-ride-tracking',
  imports: [Map, MatButtonModule, FormsModule],
  templateUrl: './ride-tracking.html',
  styleUrl: './ride-tracking.css',
})
export class RideTracking implements OnInit {
  @ViewChild(Map) map!: Map;

  router = inject(Router);
  authenticationService = inject(AuthenticationService);

  rideId = signal<number | null>(null);
  driverId = signal<number | null>(null);
  rendered = signal<boolean>(false);
  user = toSignal(this.authenticationService.activeUser$)

  showReport = signal<boolean>(false);
  reported = signal<boolean>(false);
  rideFinished = signal<boolean>(false);
  reviewed = signal<boolean>(false);

  startAddress = signal<string>('');
  destinationAddress = signal<string>('');

  driverRating = signal<number>(0);
  carRating = signal<number>(0);
  reviewComment = signal<string>('');

  points = signal<RoutePoint[]>([]);

  eta = signal<number>(0);

  message = "";
  stars = [1, 2, 3, 4, 5];

  constructor(
    private ws: DriverSimulationWsService,
    private api: ApiService,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.initDrive();
  }

  toggleReport(): void {
    this.showReport.update(v => !v);
  }

  report(): void {

    const rideId = this.rideId();

    if (!rideId) return;

    this.api.rideApi
      .reportRide(rideId, { message: this.message })
      .subscribe(() => {
        this.reported.set(true);
        this.showReport.set(false);
      });
  }

  private initDrive(): void {
  this.api.rideApi.getOngoingRide().subscribe((res) => {
    const ride: ActiveRideDto | null = res.data;
    if (!ride) return;

    this.rideId.set(ride.rideId);
    this.driverId.set(ride.driverId);

    this.startAddress.set(ride.routePoints.find(p => p.type === 'PICKUP')?.address || '');
    this.destinationAddress.set(ride.routePoints.find(p => p.type === 'DROPOFF')?.address || '');

    const mappedPoints: RoutePoint[] = ride.routePoints.map((p, index) => ({
      id: crypto.randomUUID(),
      lat: p.lat,
      lng: p.lng,
      address: p.address,
      type: p.type,
      orderIndex: p.orderIndex ?? index
    }));

    this.points.set(mappedPoints);

    if (ride.driverId) {
      this.startTrackingDriver(ride);
    }
  });
}

  private startTrackingDriver(ride: any): void {
    this.ws.connect(
      () => {},
      () => {},
      undefined,
      (pos) => {
        const driverId = this.driverId();

        if (!driverId || pos.driverId !== driverId) return;

        if (pos.finished) {
          console.log("WebSocket: Ride Finished!");
          this.rideFinished.set(true);
          return;
        }

        this.eta.set(Math.ceil(pos.eta / 60));

        if (!this.rendered()) {
          this.rendered.set(true);
          this.map.addSimulatedDriver({
            id: driverId,
            firstName: ride.driverName?.split(' ')[0] ?? '',
            lastName: ride.driverName?.split(' ')[1] ?? '',
            start: { lat: pos.lat, lng: pos.lng },
            status: ride.status as any,
          });
        } else {
          this.map.updateDriverPosition(driverId, pos.lat, pos.lng);
        }
      },
      () => {
        const user = this.user();
        if (!user) return;
        if (ride.passengerName !== user.firstName || !ride.passengerNames.includes(user.firstName)) return;
        this.router.navigate([ROUTE_USER_HOME]);
      },
      (panic) => {
        const user = this.user();
        if (!user) return;
        if (ride.passengerName !== user.firstName || !ride.passengerNames.includes(user.firstName)) return;
        this.router.navigate([ROUTE_USER_HOME]);
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
    const rideId = this.rideId();

    if (!rideId) return;

    this.api.rideApi.rateRide(rideId, {
      driverRating: this.driverRating(),
      vehicleRating: this.carRating(),
      comment: this.reviewComment(),
    }).subscribe(() => {
      this.reviewed.set(true);
    });
  }

  protected panic() {
    const rideId = this.rideId();
    const user = this.user();

    if (!rideId || !user) {
      return;
    }

    this.api.rideApi.ridePanic(rideId, { userId: user.id }).pipe(
      map(response => response.data),
    ).subscribe(rideResponse => {
      this.router.navigate([ROUTE_USER_HOME]);
    });
  }
}
