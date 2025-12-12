import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class GeocodingService {
  private nominatimUrl = 'https://nominatim.openstreetmap.org/reverse';

  constructor(private http: HttpClient) {}

  getAdressFromLatLng(lat: number, lng: number): Observable<string> {
    const url = `${this.nominatimUrl}?format=jsonv2&lat=${lat}&lon=${lng}`;

    return this.http
      .get(url, {
        headers: { 'User-Agent': 'Visor Inc- Voom/1.0' },
      })
      .pipe(
        map((response: any) => {
          return response.display_name;
        })
      );
  }
}
