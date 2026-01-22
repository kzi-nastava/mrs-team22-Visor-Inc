import {AfterViewInit, Component, effect, signal, ViewChild} from '@angular/core';
import {MatSlideToggle, MatSlideToggleChange} from '@angular/material/slide-toggle';
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
import {MatIcon} from '@angular/material/icon';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {DriverAssignedDto, RideApi,} from '../../user-pages/home/home.api';
import {RoutePoint} from '../../user-pages/home/home';
import {UserProfileApi} from '../../user-pages/user-profile/user-profile.api';
import {DriverSimulationWsService} from '../../../shared/websocket/DriverSimulationWsService';
import {map} from 'rxjs';
import {Map} from '../../../shared/map/map';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {ArrivalDialog} from '../arrival-dialog/arrival-dialog';

export const ROUTE_DRIVER_HOME = 'home';

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

type RidePhase = 'IDLE' | 'GOING_TO_PICKUP' | 'AT_PICKUP' | 'RIDE_STARTED';

@Component({
  selector: 'app-driver-home',
  imports: [
    MatSlideToggle,
    NgApexchartsModule,
    MatIcon,
    MatSnackBarModule,
    Map,
    MatDialogModule,

  ],
  templateUrl: './driver-home.html',
  styleUrl: './driver-home.css',
})
export class DriverHome implements AfterViewInit {
  @ViewChild(Map) map!: Map;
  isPassive = signal<boolean>(false);
  myId = signal<number | null>(null);
  routePoints = signal<RoutePoint[]>([]);
  toastMessage = signal<string | null>(null);
  hasArrived = signal(false);
  pickupPoint = signal<{ lat: number; lng: number } | null>(null);
  activeRideId = signal<number | null>(null);
  ridePhase = signal<RidePhase>('IDLE');

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
      if (
        this.ridePhase() === 'AT_PICKUP'
      ) {
        this.openArrivalDialog();
      }
    });
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

                const my = this.myId();
                if (my && pos.driverId === my && this.followEnabled) {
                  this.followMyDriver(pos.driverId, pos.lat, pos.lng);
                }
                if (this.pickupPoint() && !this.hasArrived()) {
                  const dist = this.distanceMeters(
                    { lat: pos.lat, lng: pos.lng },
                    this.pickupPoint()!,
                  );

                  if (dist <= 30) {
                    this.ridePhase.set('AT_PICKUP');
                  }
                }
              }
            },
          );
        },
        error: (err) => console.error(err),
      });
  }

  private openArrivalDialog() {
    this.rideApi.getActiveRide().subscribe({
      next: (res) => {
        if (!res.data) {
          console.error('No active ride on arrival');
          return;
        }

        this.activeRideId.set(res.data.rideId);

        const ref = this.dialog.open(ArrivalDialog, {
          width: '380px',
          disableClose: true,
          data: {
            pickupAddress: res.data.routePoints.find((p) => p.type === 'PICKUP')?.address ?? '',
          },
        });

        ref.afterClosed().subscribe((res) => {
          if (res === 'START') {
            this.startRide();
          }
        });
      },
      error: (err) => {
        console.error('Failed to refresh active ride', err);
      },
    });
  }

  private startRide() {
    const rideId = this.activeRideId();
    console.log('Active ride id:', rideId);
    if (!rideId) {
      console.error('No active ride id');
      return;
    }

    console.log('Starting ride...');

    const payload = {
      routePoints: this.routePoints().map((p) => ({
        lat: p.lat,
        lng: p.lng,
        orderIndex: p.order,
        type: p.type,
        address: p.address,
      })),
    };

    this.rideApi.startRide(rideId, payload).subscribe({
      next: () => {
        this.snackBar.open('Ride started', 'OK', { duration: 3000 });
        this.hasArrived.set(true);
        this.pickupPoint.set(null);
      },
      error: (err) => {
        console.error('Failed to start ride', err);
        this.snackBar.open('Failed to start ride', 'OK', { duration: 3000 });
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
        horizontalPosition: 'center',
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
          order: p.order,
        })),
    );
  }

  onToggleChange(event: MatSlideToggleChange) {
    this.isPassive.set(event.checked);
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
            order: p.orderIndex,
          }));

        this.routePoints.set(points);

        const pickup = points.find((p) => p.type === 'PICKUP');
        if (pickup) {
          this.pickupPoint.set({ lat: pickup.lat, lng: pickup.lng });
        }

        this.activeRideId.set(activeRide.rideId);

        console.log('[DriverHome] Active ride restored from API');
      },
      error: (err) => {
        console.error('[DriverHome] Failed to load active ride', err);
      },
    });
  }
}
