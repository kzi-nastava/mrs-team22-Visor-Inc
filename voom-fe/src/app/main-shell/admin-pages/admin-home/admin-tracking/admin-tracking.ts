import { Component, computed, effect, inject, signal, untracked, ViewChild } from '@angular/core';
import { MatCard } from '@angular/material/card';
import { Map } from '../../../../shared/map/map';
import { RoutePoint } from '../../../user-pages/user-home/user-home';
import { RideResponseDto } from '../../../../shared/rest/ride/ride.model';
import { catchError, map, of, startWith, switchMap } from 'rxjs';
import ApiService from '../../../../shared/rest/api-service';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { DriverSimulationWsService } from '../../../../shared/websocket/DriverSimulationWsService';
import { MatDialog } from '@angular/material/dialog';
import { PanicDialog } from '../../../../shared/panic/panic-dialog/panic-dialog';
import { MatIcon } from '@angular/material/icon';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatDrawerContent, MatDrawerContainer, MatDrawer } from "@angular/material/sidenav";
import { MatFormField, MatLabel } from "@angular/material/select";
import { ValueInputString } from '../../../../shared/value-input/value-input-string/value-input-string';

export type RoutePointDto = {
  lat: number;
  lng: number;
  orderIndex: number;
  type: 'PICKUP' | 'STOP' | 'DROPOFF';
};

export const ROUTE_ADMIN_TRACKING = "tracking"

@Component({
  selector: 'app-admin-tracking',
  imports: [Map, ReactiveFormsModule, MatCard, MatIcon, ValueInputString],
  templateUrl: './admin-tracking.html',
  styleUrl: './admin-tracking.css',
})
export class AdminTracking {
  @ViewChild(Map) map!: Map;

  private apiService = inject(ApiService);
  private ws = inject(DriverSimulationWsService);
  private dialog = inject(MatDialog);

  renderedDrivers = signal<number[]>([]);
  isSupportChatOpen = signal<boolean>(false);
  panic = signal<RideResponseDto | null>(null);
  routePoints = signal<RoutePointDto[]>([]);

  selectedDriverId = signal<number | null>(null);
  activeRide = toSignal(
    toObservable(this.selectedDriverId).pipe(
      switchMap(id => {
        if (!id) return of(null);

        return this.apiService.rideApi.getOngoingRideForDriver(id).pipe(
          map(res => res.data),
          catchError(() => of(null))
        );
      })
    ),
    { initialValue: null }
  );

  pickupPoint = computed(() => {
  return this.activeRide()?.routePoints.find(p => p.type === 'PICKUP');
});

dropoffPoint = computed(() => {
  return this.activeRide()?.routePoints.find(p => p.type === 'DROPOFF');
});

  activeDrivers$ = this.apiService.rideApi.getActiveDrivers().pipe(
    map(result => result.data ?? []),
  );

  searchFormControl = new FormControl<string>('');
  searchTerm = toSignal(
    this.searchFormControl.valueChanges.pipe(startWith('')),
    { initialValue: '' }
  );

  private allActiveDrivers = toSignal(
    this.apiService.rideApi.getActiveDrivers().pipe(map(res => res.data ?? [])),
    { initialValue: [] }
  );

  filteredDrivers = computed(() => {
    const term = this.searchTerm()?.toLowerCase() ?? '';
    const drivers = this.allActiveDrivers();
    if (!term) return drivers;

    return drivers.filter(d =>
      d.firstName.toLowerCase().includes(term) ||
      d.lastName.toLowerCase().includes(term)
    );
  });



  constructor() {
    this.activeDrivers$.pipe(
      takeUntilDestroyed(),
    ).subscribe((activeDrivers) => {
      const drivers = activeDrivers;
      if (drivers.length === 0) return;
      this.ws.connect(
        () => { },
        () => { },
        undefined,
        (pos) => {

          const selectedId = this.selectedDriverId();
          if (selectedId !== null && pos.driverId !== selectedId) {
            return;
          }

          const driver = drivers.find(d => d.id === pos.driverId);
          const renderedDrivers = this.renderedDrivers();

          if (!renderedDrivers.includes(pos.driverId)) {
            const updatedRenderedDrivers = [...renderedDrivers, pos.driverId];
            this.renderedDrivers.set(updatedRenderedDrivers);

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
        },
        () => { },
        (panic) => {
          console.log(panic);
          const ride = this.panic();
          if (!ride) {
            this.panic.set(panic);
          }
        }
      );
    });

    effect(() => {
      const panic = this.panic();
      if (!panic) return;

      const ref = this.dialog.open(PanicDialog, { data: panic });

      ref.afterClosed().subscribe(() => {
        this.panic.set(null);
      });
    });

    effect(() => {
    const ride = this.activeRide();
    
    if (ride && ride.routePoints) {
      ride.routePoints.forEach(point => {
        if (point.orderIndex === null) {
          point.orderIndex = 0;
        }
      });
      this.routePoints.set(ride.routePoints);
    } else {
      this.routePoints.set([]);
    }
  });

    effect(() => {
      const selectedId = this.selectedDriverId();

      untracked(() => {
        const currentRendered = this.renderedDrivers();

        if (selectedId !== null) {
          const driversToRemove = currentRendered.filter(id => id !== selectedId);
          driversToRemove.forEach(id => this.map.removeSimulatedDriver(id));
          const isAlreadyRendered = currentRendered.includes(selectedId);
          this.renderedDrivers.set(isAlreadyRendered ? [selectedId] : []);
        }
      });
    });
  }
}
