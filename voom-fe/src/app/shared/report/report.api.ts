import { Inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiClient } from '../rest/api-client';
import { ApiResponse, RequestConfig } from '../rest/rest.model';
import { VoomApiService } from '../rest/voom-api-service';

export type ReportDailyStatsDTO = {
  date: string;
  rideCount: number;
  totalKm: number;
  totalMoney: number;
};

export type ReportResponseDTO = {
  dailyStats: ReportDailyStatsDTO[];
  totalMoney: number;
  totalKm: number;
  totalRides: number;
  averageMoneyPerDay: number;
};

export type AdminReportResponseDTO = {
  drivers: ReportResponseDTO;
  users: ReportResponseDTO;
};

@Injectable({ providedIn: 'root' })
export class ReportApi {
  private readonly baseUrl = '/api/reports';

  constructor(@Inject(VoomApiService) private apiClient: ApiClient) {}

  getReport(from: string, to: string): Observable<ApiResponse<ReportResponseDTO>> {
    const config: RequestConfig = {
      headers: { accept: 'application/json' },
      authenticated: true,
      params: { from, to },
    };

    return this.apiClient.get<unknown, ReportResponseDTO>(this.baseUrl, config);
  }

  getAdminReport(from: string, to: string): Observable<ApiResponse<ReportResponseDTO>> {
    const config: RequestConfig = {
      headers: { accept: 'application/json' },
      authenticated: true,
      params: { from, to },
    };

    return this.apiClient.get<unknown, ReportResponseDTO>(`${this.baseUrl}/admin`, config);
  }
}
