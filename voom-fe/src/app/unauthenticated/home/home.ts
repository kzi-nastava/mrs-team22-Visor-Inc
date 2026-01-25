import {Component, OnInit, signal, ViewChild} from '@angular/core';
import {MatButton} from '@angular/material/button';
import {Map} from '../../shared/map/map';
import {ValueInputString} from '../../shared/value-input/value-input-string/value-input-string';
import {Header} from '../../shared/header/header';
import {Footer} from '../../shared/footer/footer';
import {DriverSimulationWsService} from '../../shared/websocket/DriverSimulationWsService';
import ApiService from '../../shared/rest/api-service';
import {DriverSummaryDto} from '../../shared/rest/ride/ride.model';
import {RouteEstimateRequestDto, RouteEstimateResponseDto} from '../../shared/rest/route/route.model';
import {map} from 'rxjs';
import {RoutePoint} from '../../main-shell/user-pages/home/home';
import {RoutePointType} from './home.api';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {LatLng} from 'leaflet';

export const ROUTE_HOME = 'ride';

@Component({
  selector: 'app-ride',
  standalone: true,
  imports: [
    Map,
    ValueInputString,
    MatButton,
    Header,
    Footer,
    ReactiveFormsModule,
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {

  @ViewChild(Map) map!: Map;

  private renderedDrivers: number[] = [];

  routePoints = signal<RoutePoint[]>([]);
  rideEstimation = signal<RouteEstimateResponseDto | null>(null);

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

  protected onMapClick(event: { lat: number; lng: number; address: string }) {
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

    const locationToAdd: RoutePoint = {
      id: crypto.randomUUID(),
      lat: event.lat,
      lng: event.lng,
      address: cleanAddress,
      type: 'DROPOFF',
      order: 1,
    };

    const updated = [points[0], locationToAdd];
    this.routePoints.set(updated);
    this.rideForm.patchValue({ dropoff: cleanAddress });

    const routeRequestEstimate: RouteEstimateRequestDto = {
      startPoint: new LatLng(updated[0].lat, updated[0].lng),
      endPoint: new LatLng(updated[1].lat, updated[1].lng),
    };

    this.api.routeApi.getRouteEstimate(routeRequestEstimate).pipe(
      map(response => response.data),
    ).subscribe((routeEstimateResponse) => {
      this.rideEstimation.set(routeEstimateResponse);
    });
  }
}
