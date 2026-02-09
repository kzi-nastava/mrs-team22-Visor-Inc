import {Component, effect, inject, signal, ViewChild} from '@angular/core';
import {MatCard} from '@angular/material/card';
import {AdminSupportChat} from './admin-support-chat/admin-support-chat';
import {Map} from '../../../../shared/map/map';
import {RoutePoint} from '../../../user-pages/user-home/user-home';
import {RideResponseDto} from '../../../../shared/rest/ride/ride.model';
import {map} from 'rxjs';
import ApiService from '../../../../shared/rest/api-service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {DriverSimulationWsService} from '../../../../shared/websocket/DriverSimulationWsService';
import {MatDialog} from '@angular/material/dialog';
import {PanicDialog} from '../../../../shared/panic/panic-dialog/panic-dialog';
import {NotificationService} from '../../../../shared/service/notification-service';

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
  private notificationService = inject(NotificationService);

  renderedDrivers = signal<number[]>([]);
  isSupportChatOpen = signal<boolean>(false);
  panic = signal<RideResponseDto | null>(null);
  routePoints = signal<RoutePoint[]>([]);
  selectedDriverId = signal<number | null>(null);

  activeDrivers$ = this.apiService.rideApi.getActiveDrivers().pipe(
    map(result => result.data ?? []),
  );

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

    effect(() => {
      const panic = this.panic();
      if (!panic) return;

      this.notificationService.show({
        title: 'PANIC',
        message: 'Panic in a ride' ,
        type: 'warning',
        duration: 0,
      });
      const ref = this.dialog.open(PanicDialog, { data: panic });

      ref.afterClosed().subscribe(() => {
        this.panic.set(null);
      });
    });
  }

  protected openSupportChat() {
    const isOpen = this.isSupportChatOpen();
    this.isSupportChatOpen.set(!isOpen);
  }
}
