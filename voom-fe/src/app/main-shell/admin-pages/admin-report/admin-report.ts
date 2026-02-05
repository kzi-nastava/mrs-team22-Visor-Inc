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
  styleUrls: ['./report.css'],
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

  driversChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      { data: [], label: 'Rides' },
      { data: [], label: 'Kilometers' },
      { data: [], label: 'Earnings' },
    ],
  };

  usersChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [
      { data: [], label: 'Rides' },
      { data: [], label: 'Kilometers' },
      { data: [], label: 'Expenses' },
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
          this.resetCharts();
          return;
        }

        this.populateAdminCharts(res.data.drivers, res.data.users);
      },
      error: () => {
        this.resetCharts();
      },
      complete: () => {
        this.loading = false;
      },
    });
  }

  private populateAdminCharts(
    driversData: ReportResponseDTO,
    usersData: ReportResponseDTO
  ) {
    const driverLabels = driversData.dailyStats.map((d) => d.date);

    this.driversChartData = {
      labels: driverLabels,
      datasets: [
        {
          data: driversData.dailyStats.map((d) => d.rideCount),
          label: 'Rides',
        },
        {
          data: driversData.dailyStats.map((d) => d.totalKm),
          label: 'Kilometers',
        },
        {
          data: driversData.dailyStats.map((d) => d.totalMoney),
          label: 'Earnings',
        },
      ],
    };

    const userLabels = usersData.dailyStats.map((d) => d.date);

    this.usersChartData = {
      labels: userLabels,
      datasets: [
        {
          data: usersData.dailyStats.map((d) => d.rideCount),
          label: 'Rides',
        },
        {
          data: usersData.dailyStats.map((d) => d.totalKm),
          label: 'Kilometers',
        },
        {
          data: usersData.dailyStats.map((d) => d.totalMoney),
          label: 'Expenses',
        },
      ],
    };

    this.cdr.detectChanges();
    this.charts?.forEach((chart) => chart.update());
  }

  private resetCharts() {
    this.driversChartData = {
      labels: [],
      datasets: [
        { data: [], label: 'Rides' },
        { data: [], label: 'Kilometers' },
        { data: [], label: 'Earnings' },
      ],
    };

    this.usersChartData = {
      labels: [],
      datasets: [
        { data: [], label: 'Rides' },
        { data: [], label: 'Kilometers' },
        { data: [], label: 'Expenses' },
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
