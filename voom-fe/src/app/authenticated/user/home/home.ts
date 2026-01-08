import { Component, signal, computed, ViewChild } from '@angular/core';
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
  address: string;
  type: RoutePointType;
  order: number;
}

@Component({
  selector: 'app-home',
  imports: [Header, Map, Footer, Dropdown, ValueInputString, MatButton, ReactiveFormsModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class UserHome {
  @ViewChild(Map) map!: Map;

  routePoints = signal<RoutePoint[]>([]);

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

  pitstopsView = computed(() =>
    this.routePoints()
      .filter((p) => p.type === 'STOP')
      .map((p) => ({
        ...p,
        cleanAddress: p.address.replace(/\s*,?\s*Novi Sad.*$/i, '').trim(),
      }))
  );

  onMapClick(event: { lat: number; lng: number; address: string }) {
    const cleanAddress = event.address.replace(/\s*,?\s*Novi Sad.*$/i, '').trim();
    const points = this.routePoints();

    if (points.length === 0) {
      this.routePoints.set([
        {
          id: crypto.randomUUID(),
          lat: event.lat,
          lng: event.lng,
          address: cleanAddress,
          type: 'PICKUP',
          order: 0,
        },
      ]);
      this.rideForm.patchValue({ pickup: cleanAddress });
      return;
    }

    const updated = points.map((p) =>
      p.type === ('DROPOFF' as RoutePointType) ? { ...p, type: 'STOP' as RoutePointType } : p
    );

    updated.push({
      id: crypto.randomUUID(),
      lat: event.lat,
      lng: event.lng,
      address: cleanAddress,
      type: 'DROPOFF' as RoutePointType,
      order: updated.length,
    });

    this.routePoints.set(updated);
    this.rideForm.patchValue({ dropoff: cleanAddress });
  }

  setAsDropoff(id: string) {
    const points = this.routePoints();

    const pickup = points.find((p) => p.type === 'PICKUP');
    const newDropoff = points.find((p) => p.id === id);
    if (!pickup || !newDropoff) return;

    const stops = points.filter((p) => p.id !== id && p.type !== 'PICKUP');

    const updated: RoutePoint[] = [
      { ...pickup, type: 'PICKUP', order: 0 },
      ...stops.map((p, i) => ({
        ...p,
        type: 'STOP' as RoutePointType,
        order: i + 1,
      })),
      {
        ...newDropoff,
        type: 'DROPOFF' as RoutePointType,
        order: stops.length + 1,
      },
    ];

    this.routePoints.set(updated);
    this.rideForm.patchValue({ dropoff: newDropoff.address });
  }

  removePoint(id: string) {
    const updated = this.routePoints()
      .filter((p) => p.id !== id)
      .map((p, i) => ({ ...p, order: i }));

    this.routePoints.set(updated);
  }

  onMapCleared() {
    this.routePoints.set([]);
    this.rideForm.reset({
      pickup: '',
      dropoff: '',
    });
  }

  confirmRide() {
    const payload = {
      route: this.routePoints().map((p, i) => ({
        order: i,
        lat: p.lat,
        lng: p.lng,
        type: p.type,
        address: p.address,
      })),
      pickup: this.rideForm.value.pickup,
      dropoff: this.rideForm.value.dropoff,
      vehiclePrice: this.selectedVehicle,
      time: this.selectedTime,
    };

    console.log(payload);
  }
}
