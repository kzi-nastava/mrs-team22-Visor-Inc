import {AfterViewInit, Component, signal} from '@angular/core';
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
import {DriverAssignedDto, DriverSummaryDto, PREDEFINED_ROUTES, RideApi} from '../../user-pages/home/home.api';
import {RoutePoint} from '../../user-pages/home/home';
import {UserProfileApi} from '../../user-pages/user-profile/user-profile.api';
import {DriverSimulationWsService} from '../../../shared/websocket/DriverSimulationWsService';

export const ROUTE_DRIVER_HOME = 'driverHome';

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

@Component({
  selector: 'app-driver-home',
  imports: [MatSlideToggle, NgApexchartsModule, MatIcon, MatSnackBarModule],
  templateUrl: './driver-home.html',
  styleUrl: './driver-home.css',
})
export class DriverHome implements AfterViewInit {
  isPassive = signal<boolean>(false);
  myId = signal<number | null>(null);
  routePoints = signal<RoutePoint[]>([]);
  toastMessage = signal<string | null>(null);

  public chartOptions: Partial<ChartOptions>;
  public activeTimeOptions: Partial<ChartOptions>;

  constructor(
    private rideApi: RideApi,
    private profileApi: UserProfileApi,
    private driverSocket: DriverSimulationWsService,
    private snackBar: MatSnackBar
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
  }

  ngAfterViewInit() {
    this.profileApi.getMyVehicle().subscribe({
      next: (vehicle) => {
        this.myId.set(vehicle.driverId || null);
      },
    });
    this.driverSocket.connect(
      (route) => {
        // this.map.applyDriverRoute(route.driverId, route.route);
      },
      () => {},
      (assigned) => this.handleDriverAssigned(assigned)
    );

    this.rideApi.getActiveDrivers().subscribe({
      next: (drivers) => this.initDriversOnMap(drivers),
      error: (err) => console.error(err),
    });
  }

  private handleDriverAssigned(payload: DriverAssignedDto) {
    const myDriverId = this.myId();

    if (!myDriverId) return;
    const pickup = payload.route.find((p: any) => p.type === 'PICKUP');
    if (!pickup) return;

    // const driver = this.map.getDriver(payload.driverId);
    // if (driver) {
    //   driver.status = 'GOING_TO_PICKUP';
    // }
    //
    // this.driverSocket.requestRoute({
    //   driverId: myDriverId,
    //   start: driver?.marker.getLatLng(),
    //   end: { lat: pickup.lat, lng: pickup.lng },
    // });

    if (payload.driverId !== myDriverId) return;

    this.snackBar.open(
      `You are assigned to ride, pickup adress is ${
        payload.route.find((p) => p.type === 'PICKUP')?.address || ''
      }`,
      'OK',
      {
        duration: 5000,
        verticalPosition: 'bottom',
        horizontalPosition: 'center',
      }
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
        }))
    );
  }

  private initDriversOnMap(drivers: DriverSummaryDto[]) {
    drivers.forEach((driver, index) => {
      const routeDef = PREDEFINED_ROUTES[index % PREDEFINED_ROUTES.length];

      // this.map.addSimulatedDriver({
      //   id: driver.id,
      //   firstName: driver.firstName,
      //   lastName: driver.lastName,
      //   start: routeDef.start,
      //   status: 'FREE',
      // });

      this.driverSocket.requestRoute({
        driverId: driver.id,
        start: routeDef.start,
        end: routeDef.end,
      });
    });
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
}
