import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export type RegisterDriverRequestDto = {
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
    address: string;
    vehicle: {
        model: string;
        vehicleType: 'STANDARD' | 'LUXURY' | 'VAN';
        licensePlate: string;
        numberOfSeats: number;
        babySeat: boolean;
        petFriendly: boolean;
    };
};

@Injectable({
  providedIn: 'root',
})
export class RegisterDriverApi {
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  registerDriver(body: RegisterDriverRequestDto): Observable<void> {
    return this.http.post<void>(
      `${this.baseUrl}/drivers`,
      body
    );
  }
}
