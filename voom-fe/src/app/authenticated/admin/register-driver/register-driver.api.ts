import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RegisterDriverApi {
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  registerDriver(body: FormData): Observable<void> {
    return this.http.post<void>(
      `${this.baseUrl}/admin/drivers`,
      body
    );
  }
}
