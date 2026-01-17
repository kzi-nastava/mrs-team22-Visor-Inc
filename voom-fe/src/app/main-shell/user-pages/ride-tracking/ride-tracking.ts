import { Component, ViewChild } from '@angular/core';
import { Map } from '../../../shared/map/map';
import { Header } from '../../../shared/header/header';
import { DriverSummaryDto } from '../home/home.api';
import { DriverSimulationWsService } from '../../../shared/websocket/DriverSimulationWsService';
import ApiService from '../../../shared/rest/api-service';
import { ActivatedRoute } from '@angular/router';


export const ROUTE_RIDE_TRACKING = 'ride/tracking/:rideId';

@Component({
  selector: 'app-ride-tracking',
  imports: [Header, Map],
  templateUrl: './ride-tracking.html',
  styleUrl: './ride-tracking.css',
})
export class RideTracking {

    @ViewChild(Map) map!: Map;

    private driverId!: number;
    private rendered = false;


  constructor(
    private ws: DriverSimulationWsService,
    private api: ApiService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.driverId = Number(this.route.snapshot.paramMap.get('rideId'));
    if (!this.driverId) {
      throw new Error('driverId missing in route'); // DELETE LATER AAAAAAAAAAAAAAAAAAA
    }
    this.loadActiveDrivers();
  }

  private loadActiveDrivers(): void {
  this.api.rideApi.getActiveDrivers().subscribe(res => {
    const drivers: DriverSummaryDto[] = res.data ?? [];
    const driver = drivers.find(d => d.id === this.driverId);

    if (!driver) {
      return;
    }

    this.ws.connect(
      () => {},
      () => {},
      undefined,
      (pos) => {
        if (pos.driverId !== this.driverId) return;

        if (!this.rendered) {
          this.rendered = true;

          this.map.addSimulatedDriver({
            id: driver.id,
            firstName: driver.firstName,
            lastName: driver.lastName,
            start: {
              lat: pos.lat,
              lng: pos.lng,
            },
            status: driver.status as any,
          });
        } else {
          this.map.updateDriverPosition(driver.id, pos.lat, pos.lng);
        }
      }
    );
  });
}


}
