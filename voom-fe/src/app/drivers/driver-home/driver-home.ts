import { Component, signal, ViewChild, AfterViewInit } from '@angular/core';
import { Header } from '../../core/layout/header-kt1/header-kt1';
import { Map } from '../../shared/map/map';
import { Footer } from '../../core/layout/footer/footer';
import { MatSlideToggle, MatSlideToggleChange } from '@angular/material/slide-toggle';
import {
  ApexChart,
  ApexDataLabels,
  ApexNonAxisChartSeries,
  ApexPlotOptions,
  ApexResponsive,
  ApexXAxis,
  ApexLegend,
  NgApexchartsModule,
} from 'ng-apexcharts';
import { MatIcon } from '@angular/material/icon';
import {
  DriverSummaryDto,
  PREDEFINED_ROUTES,
  RideApi,
} from '../../authenticated/user/home/home.api';
import { DriverSimulationWsService } from '../../shared/websocket/DriverSimulationWsService';
import { UserProfileApi } from '../../authenticated/user/user-profile/user-profile.api';

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
  imports: [Header, Map, Footer, MatSlideToggle, NgApexchartsModule, MatIcon],
  templateUrl: './driver-home.html',
  styleUrl: './driver-home.css',
})
export class DriverHome implements AfterViewInit {
  @ViewChild(Map) map!: Map;

  isPassive = signal<boolean>(false);
  myId = signal<number | null>(null);

  public chartOptions: Partial<ChartOptions>;
  public activeTimeOptions: Partial<ChartOptions>;

  constructor(
    private rideApi: RideApi,
    private profileApi: UserProfileApi,
    private driverSocket: DriverSimulationWsService
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
    this.driverSocket.connect(
      (route) => {
        this.map.applyDriverRoute(route.driverId, route.route);
      },
      () => {}
    );

    this.rideApi.getActiveDrivers().subscribe({
      next: (drivers) => this.initDriversOnMap(drivers),
      error: (err) => console.error(err),
    });

    this.profileApi.getMyVehicle().subscribe({
      next: (vehicle) => {
        this.myId.set(vehicle.driverId ?? null);
        console.log('My driver ID is:', vehicle.driverId);
      },
    });
  }

  private initDriversOnMap(drivers: DriverSummaryDto[]) {
    drivers.forEach((driver, index) => {
      const routeDef = PREDEFINED_ROUTES[index % PREDEFINED_ROUTES.length];

      this.map.addSimulatedDriver({
        id: driver.id,
        firstName: driver.firstName,
        lastName: driver.lastName,
        start: routeDef.start,
        status: 'FREE',
      });

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
