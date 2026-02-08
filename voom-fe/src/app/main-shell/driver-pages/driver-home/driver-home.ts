import { AfterViewInit, Component, effect, inject, signal, ViewChild } from '@angular/core';
import { MatSlideToggle, MatSlideToggleChange } from '@angular/material/slide-toggle';
import {
  ApexChart,
  ApexDataLabels,
  ApexLegend,
  ApexNonAxisChartSeries,
  ApexPlotOptions,
  ApexResponsive,
  ApexXAxis,
  NgApexchartsModule,
} from 'ng-apexcharts';
import { MatIcon } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DriverAssignedDto, RideApi } from '../../user-pages/user-home/home.api';
import { RoutePoint } from '../../user-pages/user-home/user-home';
import { UserProfileApi } from '../../user-pages/user-profile/user-profile.api';
import { DriverSimulationWsService } from '../../../shared/websocket/DriverSimulationWsService';
import { BehaviorSubject, catchError, filter, map, merge, of, switchMap } from 'rxjs';
import { Map } from '../../../shared/map/map';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ArrivalDialog } from '../arrival-dialog/arrival-dialog';
import { DriverStateChangeDto } from '../../../shared/rest/driver/driver-activity.model';
import ApiService from '../../../shared/rest/api-service';
import { AuthenticationService } from '../../../shared/service/authentication-service';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FinishRideDialog } from '../finish-ride-dialog/finish-ride-dialog';
import { MatDivider } from '@angular/material/list';
import { MatButton } from '@angular/material/button';
import { RideStopDto } from '../../../shared/rest/ride/ride.model';
import { LatLng } from 'leaflet';

export const ROUTE_DRIVER_HOME = 'ride';

export type ChartOptions = {
  series: ApexNonAxisChartSeries;
  chart: ApexChart;
  responsive: ApexResponsive[];
  plotOptions: ApexPlotOptions;
  labels: any;
  colors: string[];
  dataLabels: ApexDataLabels;
  legend: ApexLegend;
  xaxis?: ApexXAxis;
};

type RidePhase = 'IDLE' | 'GOING_TO_PICKUP' | 'AT_PICKUP' | 'RIDE_STARTED' | 'AT_DROPOFF';

@Component({
  selector: 'app-driver-ride',
  imports: [
    MatSlideToggle,
    NgApexchartsModule,
    MatIcon,
    MatSnackBarModule,
    Map,
    MatDialogModule,
    MatDivider,
    MatButton,
  ],
  templateUrl: './driver-home.html',
  styleUrl: './driver-home.css',
})
export class DriverHome implements AfterViewInit {
  @ViewChild(Map) map!: Map;
  isPassive = signal<boolean>(false);
  myId = signal<number | null>(null);
  routePoints = signal<RoutePoint[]>([]);
  hasArrived = signal(false);
  pickupPoint = signal<{ lat: number; lng: number } | null>(null);
  activeRideId = signal<number | null>(null);
  ridePhase = signal<RidePhase>('IDLE');
  isSuspended = signal<boolean>(false);
  blockReason = signal<string | null>(null);

  dropoffPoint = signal<{ lat: number; lng: number } | null>(null);
  finishDialogOpen = signal<boolean>(false);
  currentPoint = signal<{ lat: number; lng: number } | null>(null);

  private apiService = inject(ApiService);
  private authenticationService = inject(AuthenticationService);

  driver = toSignal(this.authenticationService.activeUser$);

  initialDriverState$ = this.authenticationService.activeUser$.pipe(
    filter((user) => !!user),
    takeUntilDestroyed(),
    switchMap((user) => {
      return this.apiService.driverActivityApi.getDriverState(user.id).pipe(
        map((response) => response.data),
        catchError((error) => {
          this.snackBar.open('There was an error', '', {
            horizontalPosition: 'right',
            verticalPosition: 'bottom',
          });
          return of(null);
        }),
      );
    }),
  );

  driverStateUpdate = new BehaviorSubject<DriverStateChangeDto | null>(null);

  driverState$ = merge(this.initialDriverState$, this.driverStateUpdate.asObservable()).pipe(
    map((driverState) => driverState?.currentState ?? 'INACTIVE'),
  );

  driverState = toSignal(this.driverState$);

  public chartOptions: Partial<ChartOptions>;
  public activeTimeOptions: Partial<ChartOptions>;

  constructor(
    private rideApi: RideApi,
    private profileApi: UserProfileApi,
    private driverSocket: DriverSimulationWsService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
  ) {
    this.chartOptions = {
      series: [3, 2],
      chart: {
        height: 400,
        width: '100%',
        type: 'pie',
      },
      labels: ['Finished', 'Cancelled'],
      colors: ['#4a68d2', '#e74c3c'],
      dataLabels: { enabled: true },
      legend: {
        position: 'bottom',
      },
      responsive: [],
    };

    this.activeTimeOptions = {
      series: [
        {
          name: 'Drive Time',
          data: [75],
        },
      ],
      chart: {
        type: 'bar',
        height: 50,
        sparkline: { enabled: true },
      },
      plotOptions: {
        bar: {
          horizontal: true,
          barHeight: '40%',
        },
      },
      colors: ['#4a68d2'],
      xaxis: {
        categories: ['Progress'],
        max: 100,
      },
    };

    effect(() => {
      const ridePhase = this.ridePhase();
      console.log(ridePhase);
      if (ridePhase === 'AT_PICKUP') {
        this.openArrivalDialog();
      } else if (ridePhase === 'AT_DROPOFF') {
        this.openFinishRideDialog();
      }
    });
  }

  onToggleChange(event: MatSlideToggleChange) {
    const user = this.driver();

    if (!user) {
      return;
    }

    const dto: DriverStateChangeDto = {
      userId: user.id,
      currentState: event.checked ? 'ACTIVE' : 'INACTIVE',
      performedAt: new Date().toISOString(),
    };

    this.apiService.driverActivityApi
      .changeDriverState(dto)
      .pipe(
        map((response) => response.data),
        catchError((error) => {
          this.snackBar.open('There was an error, ', error);
          return of(null);
        }),
      )
      .subscribe((driverActivityState) => {
        if (driverActivityState) {
          this.snackBar.open('Driver state updated successfully', '', {
            horizontalPosition: 'right',
            verticalPosition: 'bottom',
          });
        } else {
          this.snackBar.open('Driver state update failed', '', {
            horizontalPosition: 'right',
            verticalPosition: 'bottom',
          });
        }
        this.driverStateUpdate.next(driverActivityState);
      });

    this.isPassive.set(event.checked);
  }

  renderedDrivers: number[] = [];
  private followEnabled = true;

  private focusMyDriver(id: number) {
    this.map.focusDriver(id, 16);
  }

  private followMyDriver(id: number, lat: number, lng: number) {
    this.map.panTo(lat, lng);
  }

  ngAfterViewInit() {
    this.profileApi.getMyVehicle().subscribe({
      next: (response) => {
        const id = response.data?.driverId || null;
        this.myId.set(id);

        this.loadActiveRideFromApi();

        if (id && this.followEnabled) {
          this.focusMyDriver(id);
        }
      },
    });

    this.rideApi
      .getActiveDrivers()
      .pipe(map((r) => r.data ?? []))
      .subscribe({
        next: (drivers) => {
          if (!drivers.length) return;

          this.driverSocket.connect(
            () => {},
            () => {},
            (assigned) => this.handleDriverAssigned(assigned),
            (pos) => {
              const driver = drivers.find((d) => d.id === pos.driverId);

              if (!this.renderedDrivers.includes(pos.driverId)) {
                this.renderedDrivers.push(pos.driverId);

                this.map.addSimulatedDriver({
                  id: pos.driverId,
                  firstName: driver?.firstName ?? '',
                  lastName: driver?.lastName ?? '',
                  start: { lat: pos.lat, lng: pos.lng },
                  status: (driver?.status as any) || 'FREE',
                });

                const my = this.myId();
                if (my && pos.driverId === my && this.followEnabled) {
                  this.focusMyDriver(my);
                }
              } else {
                this.map.updateDriverPosition(pos.driverId, pos.lat, pos.lng);

                this.currentPoint.set({ lat: pos.lat, lng: pos.lng });

                const my = this.myId();
                if (my && pos.driverId === my && this.followEnabled) {
                  this.followMyDriver(pos.driverId, pos.lat, pos.lng);
                }

                if (this.pickupPoint() && !this.hasArrived()) {
                  const dist = this.distanceMeters(
                    { lat: pos.lat, lng: pos.lng },
                    this.pickupPoint()!,
                  );

                  console.log('Distance to pickup:', dist);

                  if (dist <= 30) {
                    this.ridePhase.set('AT_PICKUP');
                    this.finishDialogOpen.set(false);
                  }
                } else if (this.ridePhase() === 'RIDE_STARTED' && this.dropoffPoint()) {
                  const dist = this.distanceMeters(
                    { lat: pos.lat, lng: pos.lng },
                    this.dropoffPoint()!,
                  );
                  const finishDialogOpen = this.finishDialogOpen();

                  if ((dist <= 30 || pos.finished) && !finishDialogOpen) {
                    this.ridePhase.set('AT_DROPOFF');
                    this.finishDialogOpen.set(true);
                  }
                } else {
                  const ridePhase = this.ridePhase();

                  if (ridePhase === 'AT_DROPOFF' || ridePhase === 'AT_PICKUP') {
                    return;
                  }

                  if (ridePhase === 'GOING_TO_PICKUP') {
                    return;
                  }

                  if (this.hasArrived()) {
                    this.ridePhase.set('RIDE_STARTED');
                  }
                }
              }
            },
            () => {},
            (panic) => {
              if (panic) {
                this.activeRideId.set(null);
                this.routePoints.set([]);
                this.ridePhase.set('IDLE');
                this.finishDialogOpen.set(false);
                this.snackBar.open('Ride status ' + panic.status, '', {
                  duration: 3000,
                  horizontalPosition: 'right',
                });
              }
            },
          );
        },
        error: (err) => console.error(err),
      });

    this.authenticationService.activeUser$
      .pipe(
        filter((u) => !!u),
        switchMap((user) =>
          this.apiService.userApi.getActiveBlockNote(user.id).pipe(
            map((res) => res.data),
            catchError((err) => {
              return of(null);
            }),
          ),
        ),
      )
      .subscribe((note) => {
        if (note && note.active) {
          this.isSuspended.set(true);
          this.blockReason.set(note.reason);
        }
      });
  }

  private openArrivalDialog() {
    this.rideApi
      .getActiveRide()
      .pipe(
        map((result) => result.data),
        catchError((error) => {
          this.snackBar.open(error, '', { duration: 3000, horizontalPosition: 'right' });
          return of(null);
        }),
      )
      .subscribe((activeRide) => {
        if (!activeRide) {
          return;
        }

        this.activeRideId.set(activeRide.rideId);

        this.dialog
          .open(ArrivalDialog, {
            width: '380px',
            disableClose: true,
            data: {
              pickupAddress: activeRide.routePoints.find((p) => p.type === 'PICKUP')?.address ?? '',
              activeRide: activeRide,
            },
          })
          .afterClosed()
          .subscribe((res) => {
            if (res === 'START') {
              this.hasArrived.set(true);
              this.pickupPoint.set(null);
              this.ridePhase.set('RIDE_STARTED');
            } else {
              this.activeRideId.set(null);
              this.routePoints.set([]);
              this.ridePhase.set('IDLE');
            }
          });
      });
  }

  private openFinishRideDialog() {
    const finishDialogOpen = this.finishDialogOpen();
    if (!finishDialogOpen) return;
    this.finishDialogOpen.set(true);

    const ref = this.dialog.open(FinishRideDialog, {
      width: '380px',
      disableClose: true,
      data: {
        dropoffAddress: this.routePoints().find((p) => p.type === 'DROPOFF')?.address ?? '',
      },
    });

    ref.afterClosed().subscribe((res) => {
      if (res === 'FINISH') {
        this.finishRide();
      }
    });
  }

  public finishRide() {
    const rideId = this.activeRideId();
    if (!rideId) {
      console.error('No active ride id');
      return;
    }
    this.rideApi.finishOngoingRide().subscribe({
      next: () => {
        this.snackBar.open('Ride finished', 'OK', { duration: 3000 });
        this.activeRideId.set(null);
        this.routePoints.set([]);
        this.ridePhase.set('IDLE');
      },
      error: (err) => {
        console.error('Failed to finish ride', err);
        this.snackBar.open('Failed to finish ride', 'OK', { duration: 3000 });
      },
    });
  }

  private handleDriverAssigned(payload: DriverAssignedDto) {
    const myDriverId = this.myId();

    if (!myDriverId) return;
    const pickup = payload.route.find((p: any) => p.type === 'PICKUP');

    this.pickupPoint.set({
      lat: pickup?.lat || 0,
      lng: pickup?.lng || 0,
    });
    this.hasArrived.set(false);

    if (!pickup) return;

    const dropoff = payload.route.find((p: any) => p.type === 'DROPOFF');
    this.dropoffPoint.set({
      lat: dropoff?.lat || 0,
      lng: dropoff?.lng || 0,
    });

    if (payload.driverId !== myDriverId) return;

    this.activeRideId.set(payload.rideId);
    this.hasArrived.set(false);

    this.snackBar.open(
      `You are assigned to ride, pickup adress is ${
        payload.route.find((p) => p.type === 'PICKUP')?.address || ''
      }`,
      'OK',
      {
        duration: 5000,
        verticalPosition: 'bottom',
        horizontalPosition: 'right',
      },
    );

    this.routePoints.set(
      payload.route
        .sort((a, b) => a.order - b.order)
        .map((p) => ({
          id: crypto.randomUUID(),
          lat: p.lat,
          lng: p.lng,
          address: '',
          type: p.type,
          orderIndex: p.order,
        })),
    );
  }

  getFormattedDate() {
    const date = new Date();
    return `Today - ${date.toLocaleDateString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    })}`;
  }

  protected readonly Date = Date;

  distanceMeters(a: { lat: number; lng: number }, b: { lat: number; lng: number }): number {
    const R = 6371000;
    const dLat = ((b.lat - a.lat) * Math.PI) / 180;
    const dLng = ((b.lng - a.lng) * Math.PI) / 180;

    const sa = Math.sin(dLat / 2);
    const sb = Math.sin(dLng / 2);

    const c =
      sa * sa + Math.cos((a.lat * Math.PI) / 180) * Math.cos((b.lat * Math.PI) / 180) * sb * sb;

    return 2 * R * Math.atan2(Math.sqrt(c), Math.sqrt(1 - c));
  }

  private loadActiveRideFromApi() {
    this.rideApi.getActiveRide().subscribe({
      next: (res) => {
        if (!res.data) return;

        const activeRide = res.data;
        const points: RoutePoint[] = activeRide.routePoints
          .slice()
          .sort((a, b) => a.orderIndex - b.orderIndex)
          .map((p) => ({
            id: crypto.randomUUID(),
            lat: p.lat,
            lng: p.lng,
            address: p.address,
            type: p.type,
            orderIndex: p.orderIndex,
          }));

        this.routePoints.set(points);

        const pickup = points.find((p) => p.type === 'PICKUP');
        if (pickup) {
          this.pickupPoint.set({ lat: pickup.lat, lng: pickup.lng });
        }

        const dropoff = points.find((p) => p.type === 'DROPOFF');
        if (dropoff) {
          this.dropoffPoint.set({ lat: dropoff.lat, lng: dropoff.lng });
        }

        if (activeRide.status === 'STARTED') {
          this.ridePhase.set('RIDE_STARTED');
          this.hasArrived.set(true);
          this.activeRideId.set(activeRide.rideId);
          console.log('Ride phase', this.ridePhase());
        } else if (activeRide.status === 'ONGOING') {
          this.ridePhase.set('GOING_TO_PICKUP');
          this.hasArrived.set(false);
        } else {
          this.ridePhase.set('IDLE');
        }

        console.log('[DriverHome] Active ride restored from API');
      },
      error: (err) => {
        console.error('[DriverHome] Failed to load active ride', err);
      },
    });
  }

  protected stopRide() {
    const rideId = this.activeRideId();
    const user = this.driver();

    const endPoint = this.currentPoint();

    if (!user || !rideId || !endPoint) return;

    const dto: RideStopDto = {
      userId: user.id,
      point: endPoint as LatLng,
      timestamp: new Date().toISOString(),
    };

    this.apiService.rideApi
      .stopRide(rideId, dto)
      .pipe(map((response) => response.data))
      .subscribe((rideResponse) => {
        this.activeRideId.set(null);
        this.routePoints.set([]);
        this.ridePhase.set('IDLE');
      });
  }

  protected panic() {
    const rideId = this.activeRideId();
    const user = this.driver();

    if (!rideId || !user) {
      return;
    }

    this.apiService.rideApi
      .ridePanic(rideId, { userId: user.id })
      .pipe(map((response) => response.data))
      .subscribe((rideResponse) => {
        this.activeRideId.set(null);
        this.routePoints.set([]);
        this.ridePhase.set('IDLE');
      });
  }
}
