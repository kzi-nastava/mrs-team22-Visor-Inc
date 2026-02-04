import { ChangeDetectorRef, Component, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { ReportApi } from './report.api';
import { ReportResponseDTO } from './report.api';
import { Chart, registerables } from 'chart.js';
import { AuthenticationService } from '../service/authentication-service';

Chart.register(...registerables);

export const ROUTE_STATISTICS = 'statistics';

@Component({
  selector: 'app-report',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    BaseChartDirective,
  ],
  templateUrl: './report.html',
  styleUrls: ['./report.css'],
})
export class Report {
  @ViewChildren(BaseChartDirective) charts?: QueryList<BaseChartDirective>;

  constructor(
    private reportApi: ReportApi,
    private authService: AuthenticationService,
    private cdr: ChangeDetectorRef,
  ) {}

  isDriver = false;
  isUser = false;
  fromDate?: Date;
  toDate?: Date;

  loading = false;

  summary = {
    totalRides: 0,
    totalKm: 0,
    totalMoney: 0,
    averageMoney: 0,
  };

  ridesChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Rides per day' }],
  };

  kmChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Kilometers per day' }],
  };

  moneyChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Money per day' }],
  };

  chartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
  };

  ngOnInit() {
    const user = this.authService.activeUser$.value;
    this.isDriver = user?.role === 'DRIVER';
    this.isUser = user?.role === 'USER';
  }

  loadReports() {
    if (!this.fromDate || !this.toDate) return;

    if (!this.isDateRangeValid()) {
      return;
    }

    this.loading = true;

    const from = this.formatDate(this.fromDate);
    const to = this.formatDate(this.toDate);

    this.reportApi.getReport(from, to).subscribe({
      next: (res) => {
        if (!res?.data) {
          this.resetCharts();
          return;
        }

        this.populateCharts(res.data);
      },
      error: (err) => {
        console.error('Report error:', err);
        this.resetCharts();
      },
      complete: () => {
        this.loading = false;
      },
    });
  }

  private populateCharts(data: ReportResponseDTO) {
    const days = this.calculateDaysBetween(this.fromDate!, this.toDate!);

    const average = days > 0 ? Math.round((data.totalMoney / days) * 100) / 100 : 0;

    this.summary = {
      totalRides: data.totalRides,
      totalKm: data.totalKm,
      totalMoney: data.totalMoney,
      averageMoney: average,
    };

    const labels = data.dailyStats.map((d) => d.date);
    const rides = data.dailyStats.map((d) => d.rideCount);
    const kms = data.dailyStats.map((d) => parseFloat(d.totalKm.toFixed(2)));
    const money = data.dailyStats.map((d) => d.totalMoney);

    this.ridesChartData = {
      labels,
      datasets: [{ data: rides, label: 'Rides per day' }],
    };

    this.kmChartData = {
      labels,
      datasets: [{ data: kms, label: 'Kilometers per day' }],
    };

    this.moneyChartData = {
      labels,
      datasets: [
        {
          data: money,
          label: this.isDriver ? 'Earnings per day' : 'Expenses per day',
        },
      ],
    };

    this.cdr.detectChanges();
    this.charts?.forEach((chart) => chart.update());
  }

  private resetCharts() {
    this.summary = {
      totalRides: 0,
      totalKm: 0,
      totalMoney: 0,
      averageMoney: 0,
    };

    this.ridesChartData = { labels: [], datasets: [{ data: [], label: 'Rides per day' }] };
    this.kmChartData = { labels: [], datasets: [{ data: [], label: 'Kilometers per day' }] };
    this.moneyChartData = { labels: [], datasets: [{ data: [], label: 'Money per day' }] };
  }

  private formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  private calculateDaysBetween(from: Date, to: Date): number {
    const start = new Date(from);
    const end = new Date(to);

    start.setHours(0, 0, 0, 0);
    end.setHours(0, 0, 0, 0);

    const diffMs = end.getTime() - start.getTime();
    const diffDays = diffMs / (1000 * 60 * 60 * 24);

    return diffDays + 1;
  }

  private isDateRangeValid(): boolean {
    if (!this.fromDate || !this.toDate) return false;

    const from = new Date(this.fromDate);
    const to = new Date(this.toDate);

    from.setHours(0, 0, 0, 0);
    to.setHours(0, 0, 0, 0);

    return from.getTime() <= to.getTime();
  }

  isInvalidRange(): boolean {
    return !!this.fromDate && !!this.toDate && this.fromDate > this.toDate;
  }
}
