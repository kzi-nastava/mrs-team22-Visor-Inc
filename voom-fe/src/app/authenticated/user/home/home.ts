import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Map } from '../../../shared/map/map';
import { Footer } from '../../../core/layout/footer/footer';
import { Header } from '../../../core/layout/header-kt1/header-kt1';
import { Dropdown } from '../../../shared/dropdown/dropdown';
import { ValueInputString } from '../../../shared/value-input/value-input-string/value-input-string';
import { MatButton } from '@angular/material/button';

export const ROUTE_USER_HOME = 'user/home';

type RoutePointType = 'PICKUP' | 'STOP' | 'DROPOFF';

interface RoutePoint {
  id: string;
  lat: number;
  lng: number;
  type: RoutePointType;
  order: number;
}

@Component({
  selector: 'app-home',
  imports: [
    Header,
    Map,
    Footer,
    Dropdown,
    ValueInputString,
    MatButton,
    ReactiveFormsModule,
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class UserHome {
  routePoints: RoutePoint[] = [];

  // 👉 dropdown state (OBAVEZNO jer koristiš [(selected)])
  selectedVehicle: number | null = null;
  selectedTime: string = 'now';

  rideForm = new FormGroup({
    pickup: new FormControl<string>(''),
    dropoff: new FormControl<string>(''),
  });

  vehicleOptions = [
    { label: 'Standard', value: 100 },
    { label: 'Luxury', value: 200 },
    { label: 'Van', value: 150 },
  ];

  timeOptions = [
    { label: 'Now', value: 'now' },
    { label: 'Later', value: 'later' },
  ];

  onMapClick(event: { lat: number; lng: number }) {
    this.addPoint(event.lat, event.lng);
  }

  addPoint(lat: number, lng: number) {
    const hasPickup = this.routePoints.some(p => p.type === 'PICKUP');
    const hasDropoff = this.routePoints.some(p => p.type === 'DROPOFF');

    let type: RoutePointType = 'STOP';
    if (!hasPickup) type = 'PICKUP';
    else if (!hasDropoff) type = 'STOP';
    else return;

    this.routePoints.push({
      id: crypto.randomUUID(),
      lat,
      lng,
      type,
      order: this.routePoints.length,
    });
  }

  setAsDropoff(id: string) {
    this.routePoints = this.routePoints.map(p =>
      p.id === id
        ? { ...p, type: 'DROPOFF' }
        : p.type === 'DROPOFF'
        ? { ...p, type: 'STOP' }
        : p
    );
  }

  removePoint(id: string) {
    this.routePoints = this.routePoints
      .filter(p => p.id !== id)
      .map((p, i) => ({ ...p, order: i }));
  }

  confirmRide() {
    const payload = {
      route: this.routePoints.map((p, i) => ({
        order: i,
        lat: p.lat,
        lng: p.lng,
        type: p.type,
      })),
      pickup: this.rideForm.value.pickup,
      dropoff: this.rideForm.value.dropoff,
      vehiclePrice: this.selectedVehicle,
      time: this.selectedTime,
    };

    console.log(payload);
  }
}
