import { Component, ViewChild } from '@angular/core';
// import { Header } from '../header/header';
import { Map } from '../../shared/map/map';
import { Dropdown } from '../../shared/dropdown/dropdown';
import { ValueInputString } from '../../shared/value-input/value-input-string/value-input-string';
import { MatButton } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { DriverSimulationWsService } from '../../shared/websocket/DriverSimulationWsService';
import { RideApi } from '../../shared/rest/home/home.api';
import {Header} from '../../shared/header/header';
import {Footer} from '../../shared/footer/footer';

export const ROUTE_HOME = 'home';

@Component({
  selector: 'app-home',
  imports: [Map, Dropdown, ValueInputString, MatButton, RouterLink, Header, Footer],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
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

  drivers: number[] = [];

  @ViewChild(Map) map!: Map;

  constructor(private ws: DriverSimulationWsService, private rideApi: RideApi) {}

  loadActiveDrivers() {
  this.rideApi.getActiveDrivers().subscribe((drivers) => {
    if (!drivers || drivers.length === 0) return;

    this.ws.connect(
      () => {},
      () => {},
      undefined,
      (pos) => {
        const driver = drivers.find(d => d.id === pos.driverId);

        if (!this.drivers.includes(pos.driverId)) {
          this.drivers.push(pos.driverId);

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


  ngOnInit() {
    this.loadActiveDrivers();
  }
}
