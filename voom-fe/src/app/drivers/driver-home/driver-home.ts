import {Component, signal} from '@angular/core';
import {Header} from "../../core/layout/header-kt1/header-kt1";
import {Map} from "../../shared/map/map";
import {Footer} from '../../core/layout/footer/footer';
import {MatSlideToggle, MatSlideToggleChange} from '@angular/material/slide-toggle';
import {
  ApexChart,
  ApexDataLabels,
  ApexNonAxisChartSeries,
  ApexPlotOptions,
  ApexResponsive,
  NgApexchartsModule
} from 'ng-apexcharts';
import {MatIcon} from '@angular/material/icon';

export const ROUTE_DRIVER_HOME = 'driver-home';

export type ChartOptions = {
  series: ApexNonAxisChartSeries;
  chart: ApexChart;
  responsive: ApexResponsive[];
  plotOptions: ApexPlotOptions;
  labels: any;
  colors: string[];
  legend: ApexLegend;
  dataLabels: ApexDataLabels;
  xaxis: ApexXAxis;
};

@Component({
  selector: 'app-driver-home',
  imports: [
    Header,
    Map,
    Footer,
    MatSlideToggle,
    NgApexchartsModule,
    MatIcon
  ],
  templateUrl: './driver-home.html',
  styleUrl: './driver-home.css',
})
export class DriverHome {

  isPassive = signal<boolean>(false);
  public chartOptions: Partial<ChartOptions>;
  public activeTimeOptions: Partial<ChartOptions>;

  constructor() {
    this.chartOptions = {
      series: [3, 2],
      chart: {
        height: 400,
        width: '100%',
        type: "pie"
      } as ApexChart,
      labels: ["Finished", "Cancelled"],
      colors: ["#4a68d2", "#e74c3c"],
      dataLabels: {
        enabled: true
      },
      legend: {
        position: "bottom"
      },
      responsive: [
        {
          breakpoint: 20,
          options: {
            chart: {
              width: 10,
              height: 10,
            },
            legend: {
              position: "center"
            }
          }
        }
      ]
    };

    this.activeTimeOptions = {
      series: [{
        name: "Drive Time",
        data: [75]
      }],
      chart: {
        type: "bar",
        height: 50,
        sparkline: { enabled: true }
      } as ApexChart,
      plotOptions: {
        bar: {
          horizontal: true,
          barHeight: "40%",
          colors: {
            backgroundBarColors: ["#E0E0E0"],
            backgroundBarOpacity: 1,
          }
        }
      },
      colors: ["#4a68d2"],
      xaxis: {
        categories: ["Progress"],
        max: 100 // Ensures the bar is relative to 100%
      }
    };
  }


  getFormattedDate() {
    const date = Date.now();

    const options: Intl.DateTimeFormatOptions = {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    };

    const dateString = new Intl.DateTimeFormat('en-US', options).format(date);

    return `Today - ${dateString}`;
  }

  onToggleChange(event: MatSlideToggleChange) {
    this.isPassive.set(!this.isPassive);
  }

  protected readonly Date = Date;
}
