import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

export type ActivateProfileRequestDto = {
  token: string;
  password: string;
  confirmPassword: string;
};

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

@Injectable({
  providedIn: 'root',
})
export class ActivateProfileApi {
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  activateProfile(body: ActivateProfileRequestDto) {
    return this.http.post<void>(`${this.baseUrl}/drivers/activation`, body);
  }
}
