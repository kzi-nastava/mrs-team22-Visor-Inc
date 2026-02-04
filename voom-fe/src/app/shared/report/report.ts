import { Component } from '@angular/core';
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

export const ROUTE_STATISTICS = "statistics";

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
    BaseChartDirective
  ],
  templateUrl: './report.html',
  styleUrls: ['./report.css']
})
export class Report {

  constructor(private reportApi: ReportApi) {}

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
    datasets: [{ data: [], label: 'Rides per day' }]
  };

  kmChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Kilometers per day' }]
  };

  moneyChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{ data: [], label: 'Money per day' }]
  };

  chartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
  };

  loadReports() {
    if (!this.fromDate || !this.toDate) return;

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
      }
    });
  }

  private populateCharts(data: ReportResponseDTO) {

    // summary
    this.summary = {
      totalRides: data.totalRides,
      totalKm: data.totalKm,
      totalMoney: data.totalMoney,
      averageMoney: data.averageMoneyPerDay
    };

    const labels = data.dailyStats.map(d => d.date);
    const rides = data.dailyStats.map(d => d.rideCount);
    const kms = data.dailyStats.map(d => d.totalKm);
    const money = data.dailyStats.map(d => d.totalMoney);

    // IMPORTANT: kreiramo NOVI objekat da Chart detektuje promenu
    this.ridesChartData = {
      labels,
      datasets: [{ data: rides, label: 'Rides per day' }]
    };

    this.kmChartData = {
      labels,
      datasets: [{ data: kms, label: 'Kilometers per day' }]
    };

    this.moneyChartData = {
      labels,
      datasets: [{ data: money, label: 'Money per day' }]
    };
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
}
