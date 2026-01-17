import { Component, ViewChild } from '@angular/core';
import { Map } from '../../../shared/map/map';
import { Header } from '../../../shared/header/header';
import { DriverSummaryDto } from '../home/home.api';
import { DriverSimulationWsService } from '../../../shared/websocket/DriverSimulationWsService';
import ApiService from '../../../shared/rest/api-service';
import { ActivatedRoute } from '@angular/router';
import { RideResponseDto } from '../../../shared/rest/home/home.model';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';

export const ROUTE_RIDE_TRACKING = 'ride/tracking/:rideId';

@Component({
  selector: 'app-ride-tracking',
  imports: [Header, Map, MatButtonModule, FormsModule],
  templateUrl: './ride-tracking.html',
  styleUrl: './ride-tracking.css',
})
export class RideTracking {

    @ViewChild(Map) map!: Map;

    private rideId!: number;
    private driverId!: number;
    private rendered = false;

    showReport = false;
    message = "";
    reported = false;

    toggleReport(): void {
    this.showReport = !this.showReport;
  }

  report(): void {
    this.reported = true;
    this.toggleReport();
  }

  constructor(
    private ws: DriverSimulationWsService,
    private api: ApiService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
  this.rideId = Number(this.route.snapshot.paramMap.get('rideId'));

  if (!this.rideId) {
    throw new Error('rideId missing in route');
  }

  this.initDrive();
}

  // api/rides/:id

 private initDrive(): void {
  this.api.rideApi.getRide(this.rideId).subscribe((res) => {

    const ride: RideResponseDto | null = res.data;

    console.log(ride);

    if (!ride) {
      return;
    }

    this.driverId = ride.driverId;

    if (!this.driverId) {
      console.warn('Ride has no driver assigned');
      return;
    }

    this.map.drawRouteFromAddresses(
      ride.startAddress,
      ride.destinationAddress
    );


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

      if (!this.rendered) {
        this.rendered = true;

        this.map.addSimulatedDriver({
          id: this.driverId,
          firstName: ride.driverName.split(' ')[0] ?? '',
          lastName: ride.driverName.split(' ')[1] ?? '',
          start: {
            lat: pos.lat,
            lng: pos.lng,
          },
          status: ride.status as any,
        });
      } else {
        this.map.updateDriverPosition(this.driverId, pos.lat, pos.lng);
      }
    }
  );
}


}
