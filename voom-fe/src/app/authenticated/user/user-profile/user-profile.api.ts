import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type UserProfileResponseDto = {
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
};

export type UpdateUserProfileRequestDto = {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
};

export type UpdateUserPasswordRequestDto = {
  newPassword: string;
  confirmPassword: string;
};

@Injectable({ providedIn: 'root' })
export class UserProfileApi {
  private readonly baseUrl = 'http://localhost:8080/api/users/me';

  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserProfileResponseDto> {
    return this.http.get<UserProfileResponseDto>(this.baseUrl);
  }

  updateProfile(body: UpdateUserProfileRequestDto): Observable<UserProfileResponseDto> {
    console.log('Updating profile with data:', body);
    return this.http.put<UserProfileResponseDto>(this.baseUrl, body);
  }

  updatePassword(body: UpdateUserPasswordRequestDto): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/password`, body);
  }
}
