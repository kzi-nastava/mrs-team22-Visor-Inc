import { Component, ViewChild } from '@angular/core';
// import { Header } from '../header/header';
import { Map } from '../../shared/map/map';
import { Footer } from '../../core/layout/footer/footer';
import { Dropdown } from '../../shared/dropdown/dropdown';
import { ValueInputString } from '../../shared/value-input/value-input-string/value-input-string';
import { MatButton } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { Header } from '../../core/layout/header-kt1/header-kt1';
import { DriverSimulationWsService } from '../../shared/websocket/DriverSimulationWsService';
import { RideApi } from './home.api';

export const ROUTE_HOME = 'home';

@Component({
  selector: 'app-home',
  imports: [Header, Map, Footer, Dropdown, ValueInputString, MatButton, RouterLink],
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

  loadDriversAndInsertDickIntoMouth() {
    this.rideApi.getActiveDrivers().subscribe((drivers) => {
      console.log('ACTIVE DRIVERS:', drivers);

      this.ws.connect(
        () => {},
        () => {},
        undefined,
        (pos) => {
          if (!this.drivers.includes(pos.driverId)) {
            this.drivers.push(pos.driverId);
            const name = drivers.filter((d) => d.id === pos.driverId).at(0)?.firstName ?? '';
            const lastname = drivers.filter((d) => d.id === pos.driverId).at(0)?.lastName ?? '';
            const status = drivers.filter((d) => d.id === pos.driverId).at(0)?.status;
            this.map.addSimulatedDriver({
              id: pos.driverId,
              firstName: name,
              lastName: lastname,
              start: {
                lat: pos.lat,
                lng: pos.lng,
              },
              status: 'FREE',
            });
          } else {
            this.map.updateDriverPosition(pos.driverId, pos.lat, pos.lng);
          }
        }
      );
    });
  }

  ngOnInit() {
    this.loadDriversAndInsertDickIntoMouth();
  }
}
