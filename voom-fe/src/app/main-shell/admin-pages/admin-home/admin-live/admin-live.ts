import {Component, computed, inject, signal, ViewChild} from '@angular/core';
import {MatCard} from '@angular/material/card';
import {AdminSupportChat} from './admin-support-chat/admin-support-chat';
import {Map} from '../../../../shared/map/map';
import {RoutePoint} from '../../../user-pages/home/user-home';
import {DriverSummaryDto, RideResponseDto} from '../../../../shared/rest/ride/ride.model';
import {catchError, map, of} from 'rxjs';
import ApiService from '../../../../shared/rest/api-service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {takeUntilDestroyed, toSignal} from '@angular/core/rxjs-interop';
import {DriverSimulationWsService} from '../../../../shared/websocket/DriverSimulationWsService';
import {MatDialog} from '@angular/material/dialog';
import {AdminPanic} from './admin-panic/admin-panic';

export const ROUTE_ADMIN_LIVE = "live"

@Component({
  selector: 'app-admin-live',
  imports: [
    MatCard,
    AdminSupportChat,
    Map
  ],
  templateUrl: './admin-live.html',
  styleUrl: './admin-live.css',
})
export class AdminLive {
  @ViewChild(Map) map!: Map;

  private apiService = inject(ApiService);
  private ws = inject(DriverSimulationWsService);
  private dialog = inject(MatDialog);

  renderedDrivers = signal<number[]>([]);
  isSupportChatOpen = signal<boolean>(false);
  panic = signal<RideResponseDto | null>(null);
  routePoints = signal<RoutePoint[]>([]);

  activeDrivers$ = this.apiService.rideApi.getActiveDrivers().pipe(
    map(result => result.data ?? []),
  );

  track = computed(() => {
    const panic= this.panic();
    if (panic) {
      this.dialog.open(AdminPanic, { data: panic }).afterClosed().subscribe(() => {
        this.panic.set(null);
      });
    }
  })

  constructor() {
    this.activeDrivers$.pipe(
      takeUntilDestroyed(),
    ).subscribe((activeDrivers) => {
      const drivers = activeDrivers;
      if (drivers.length === 0) return;
      this.ws.connect(
        () => {},
        () => {},
        undefined,
        (pos) => {
          const driver = drivers.find(d => d.id === pos.driverId);
          const renderedDrivers = this.renderedDrivers();

          if (!renderedDrivers.includes(pos.driverId)) {
            const updatedRenderedDrivers = [...renderedDrivers, pos.driverId ];
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
        () => {},
        (panic) => {
          console.log(panic);
          const ride = this.panic();
          if (!ride) {
            this.panic.set(panic);
          }
        }
      );
    });
  }

  protected openSupportChat() {
    const isOpen = this.isSupportChatOpen();
    this.isSupportChatOpen.set(!isOpen);
  }
}
