import { Component, ViewChild, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButton } from '@angular/material/button';

import { Map } from '../../shared/map/map';
import { Dropdown } from '../../shared/dropdown/dropdown';
import { ValueInputString } from '../../shared/value-input/value-input-string/value-input-string';
import { Header } from '../../shared/header/header';
import { Footer } from '../../shared/footer/footer';

import { DriverSimulationWsService } from '../../shared/websocket/DriverSimulationWsService';
import ApiService from '../../shared/rest/api-service';
import { DriverSummaryDto } from '../../shared/rest/ride/ride.model';

export const ROUTE_HOME = 'ride';

@Component({
  selector: 'app-ride',
  standalone: true,
  imports: [
    Map,
    Dropdown,
    ValueInputString,
    MatButton,
    RouterLink,
    Header,
    Footer,
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {

  @ViewChild(Map) map!: Map;

  public vehicleOptions = [
    { label: 'Standard', value: 100 },
    { label: 'Luxury', value: 200 },
    { label: 'Van', value: 150 },
  ];

  public selectedVehicle = 100;

  public timeOptions = [
    { label: 'Now', value: 'now' },
    { label: 'Later', value: 'later' },
  ];

  public selectedTime = 'now';

  private renderedDrivers: number[] = [];

  constructor(
    private ws: DriverSimulationWsService,
    private api: ApiService
  ) {}

  ngOnInit(): void {
    this.loadActiveDrivers();
  }

  private loadActiveDrivers(): void {
    this.api.rideApi.getActiveDrivers().subscribe(res => {
      const drivers: DriverSummaryDto[] = res.data ?? [];
      if (drivers.length === 0) return;

      console.log('Loaded active drivers:', drivers);

      this.ws.connect(
        () => {},
        () => {},
        undefined,
        (pos) => {
          const driver = drivers.find(d => d.id === pos.driverId);

          if (!this.renderedDrivers.includes(pos.driverId)) {
            this.renderedDrivers.push(pos.driverId);

            this.map.addSimulatedDriver({
              id: pos.driverId,
              firstName: driver?.firstName ?? '',
              lastName: driver?.lastName ?? '',
              start: {
                lat: pos.lat,
                lng: pos.lng,
              },
              status: (driver?.status as any) || 'FREE',
            });
          } else {
            this.map.updateDriverPosition(pos.driverId, pos.lat, pos.lng);
          }
        }
      );
    });
  }
}
