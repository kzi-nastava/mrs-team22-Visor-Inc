import {
  ChangeDetectorRef,
  Component,
  QueryList,
  ViewChildren,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';
import { Chart, registerables } from 'chart.js';
import { ReportApi, ReportResponseDTO } from '../../../shared/report/report.api';

Chart.register(...registerables);

export const ROUTE_ADMIN_REPORT = 'reports';

@Component({
  selector: 'app-admin-report',
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
  templateUrl: './admin-report.html',
  styleUrls: ['./admin-report.css'],
})
export class AdminReport {
  @ViewChildren(BaseChartDirective) charts?: QueryList<BaseChartDirective>;

  constructor(
    private reportApi: ReportApi,
    private cdr: ChangeDetectorRef
  ) {}

  fromDate?: Date;
  toDate?: Date;
  loading = false;

  summary = {
    totalRides: 0,
    totalKm: 0,
    totalMoney: 0,
    averageMoney: 0,
  };

  systemChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      { data: [], label: 'Rides' },
      { data: [], label: 'Kilometers' },
      { data: [], label: 'Money' },
    ],
  };

  chartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
  };

  loadAdminReport() {
    if (!this.fromDate || !this.toDate) return;
    if (!this.isDateRangeValid()) return;

    this.loading = true;

    const from = this.formatDate(this.fromDate);
    const to = this.formatDate(this.toDate);

    this.reportApi.getAdminReport(from, to).subscribe({
      next: (res) => {
        if (!res?.data) {
          this.reset();
          return;
        }

        this.populate(res.data);
      },
      error: () => this.reset(),
      complete: () => (this.loading = false),
    });
  }

  private populate(data: ReportResponseDTO) {
    const days = data.dailyStats.length;

    this.summary = {
      totalRides: data.totalRides,
      totalKm: data.totalKm,
      totalMoney: data.totalMoney,
      averageMoney: days > 0 ? data.totalMoney / days : 0,
    };

    this.systemChartData = {
      labels: data.dailyStats.map(d => d.date),
      datasets: [
        {
          data: data.dailyStats.map(d => d.rideCount),
          label: 'Rides',
        },
        {
          data: data.dailyStats.map(d => d.totalKm),
          label: 'Kilometers',
        },
        {
          data: data.dailyStats.map(d => d.totalMoney),
          label: 'Money',
        },
      ],
    };

    this.cdr.detectChanges();
    this.charts?.forEach(chart => chart.update());
  }

  private reset() {
    this.summary = {
      totalRides: 0,
      totalKm: 0,
      totalMoney: 0,
      averageMoney: 0,
    };

    this.systemChartData = {
      labels: [],
      datasets: [
        { data: [], label: 'Rides' },
        { data: [], label: 'Kilometers' },
        { data: [], label: 'Money' },
      ],
    };
  }

  private formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  private isDateRangeValid(): boolean {
    if (!this.fromDate || !this.toDate) return false;
    return this.fromDate <= this.toDate;
  }
}

