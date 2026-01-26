import {Component, computed, OnInit, signal, ViewChild} from '@angular/core';
import {Map} from '../../shared/map/map';
import {ValueInputString} from '../../shared/value-input/value-input-string/value-input-string';
import {Header} from '../../shared/header/header';
import {Footer} from '../../shared/footer/footer';
import {DriverSimulationWsService} from '../../shared/websocket/DriverSimulationWsService';
import ApiService from '../../shared/rest/api-service';
import {DriverSummaryDto} from '../../shared/rest/ride/ride.model';
import {RouteEstimateRequestDto, RouteEstimateResponseDto} from '../../shared/rest/route/route.model';
import {map} from 'rxjs';
import {RoutePoint} from '../../main-shell/user-pages/home/user-home';
import {RoutePointType} from './home.api';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {MatButton} from '@angular/material/button';

export const ROUTE_HOME = 'ride';

@Component({
  selector: 'app-ride',
  standalone: true,
  imports: [
    Map,
    ValueInputString,
    Header,
    Footer,
    ReactiveFormsModule,
    MatButton,
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {

  @ViewChild(Map) map!: Map;

  private renderedDrivers: number[] = [];

  routePoints = signal<RoutePoint[]>([]);
  rideEstimation = signal<RouteEstimateResponseDto | null>(null);

  pitstopsView = computed(() =>
    this.routePoints()
      .filter((p) => p.type === 'STOP')
      .map((p) => ({
        ...p,
        cleanAddress: p.address.replace(/\s*,?\s*Novi Sad.*$/i, '').trim(),
      })),
  );

  rideForm = new FormGroup({
    pickup: new FormControl<string>(''),
    dropoff: new FormControl<string>(''),
  });

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

  removePoint(id: string) {
    this.routePoints.set(
      this.routePoints()
        .filter((p) => p.id !== id)
        .map((p, i) => ({...p, orderIndex: i})),
    );
  }

  setAsDropoff(id: string) {
    const points = this.routePoints();
    const pickup = points.find((p) => p.type === 'PICKUP');
    const newDropoff = points.find((p) => p.id === id);
    if (!pickup || !newDropoff) return;

    const stops = points.filter((p) => p.id !== id && p.type !== 'PICKUP');

    const updated: RoutePoint[] = [
      { ...pickup, orderIndex: 0 },
      ...stops.map((p, i) => ({ ...p, type: 'STOP' as RoutePointType, orderIndex: i + 1 })),
      { ...newDropoff, type: 'DROPOFF', orderIndex: stops.length + 1 },
    ];

    this.routePoints.set(updated);
    this.rideForm.patchValue({ dropoff: newDropoff.address });
  }

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
          orderIndex: 0,
        },
      ]);
      this.rideForm.patchValue({ pickup: cleanAddress });
      return;
    }

    const updated = points.map((p) =>
      p.type === 'DROPOFF' ? { ...p, type: 'STOP' as RoutePointType } : p,
    );

    updated.push({
      id: crypto.randomUUID(),
      lat: event.lat,
      lng: event.lng,
      address: cleanAddress,
      type: 'DROPOFF',
      orderIndex: updated.length,
    });

    this.routePoints.set(updated);
    this.rideForm.patchValue({ dropoff: cleanAddress });

    const routeRequestEstimate: RouteEstimateRequestDto = {
      routePoints: updated,
    };

    this.api.routeApi.getRouteEstimate(routeRequestEstimate).pipe(
      map(response => response.data),
    ).subscribe((routeEstimateResponse) => {
      this.rideEstimation.set(routeEstimateResponse);
    });
  }

  protected onMapCleared() {
    this.routePoints.set([]);
    this.rideForm.reset({
      pickup: '',
      dropoff: '',
    });
  }
}
