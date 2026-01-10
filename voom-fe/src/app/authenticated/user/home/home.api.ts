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

export type DriverVehicleResponseDto = {
  model: string;
  vehicleType: 'STANDARD' | 'LUXURY' | 'VAN';
  licensePlate: string;
  numberOfSeats: number;
  babySeat: boolean;
  petFriendly: boolean;
  activeHoursLast24h?: number;
};

export type UpdateDriverVehicleRequestDto = {
  model: string;
  vehicleType: 'STANDARD' | 'LUXURY' | 'VAN';
  licensePlate: string;
  numberOfSeats: number;
  babySeat: boolean;
  petFriendly: boolean;
};

export type RoutePointType = 'PICKUP' | 'STOP' | 'DROPOFF';

export type RideRoutePointDto = {
  lat: number;
  lng: number;
  order: number;
  type: RoutePointType;
  address: string;
};

export type ScheduledRideDto = {
  rideId: number;
  rideRequestId: number;
  driverId: number | null;
  scheduledStartTime: string;
  status: string;
  route: {
    lat: number;
    lng: number;
    order: number;
    type: 'PICKUP' | 'STOP' | 'DROPOFF';
  }[];
}

export const PREDEFINED_ROUTES = [
  // 1. Liman I → Centar
  {
    start: { lat: 45.2458, lng: 19.8529 },
    end: { lat: 45.2556, lng: 19.8449 },
  },

  // 2. Liman III → Spens
  {
    start: { lat: 45.2429, lng: 19.8434 },
    end: { lat: 45.2472, lng: 19.8372 },
  },

  // 3. Telep → Liman IV
  {
    start: { lat: 45.2384, lng: 19.8049 },
    end: { lat: 45.2441, lng: 19.8586 },
  },

  // 4. Novo Naselje → Detelinara
  {
    start: { lat: 45.2678, lng: 19.8006 },
    end: { lat: 45.2627, lng: 19.8149 },
  },

  // 5. Sajlovo → Novo Naselje
  {
    start: { lat: 45.2709, lng: 19.7753 },
    end: { lat: 45.2661, lng: 19.8062 },
  },

  // 6. Klisa → Podbara
  {
    start: { lat: 45.2813, lng: 19.8418 },
    end: { lat: 45.2692, lng: 19.8593 },
  },

  // 7. Podbara → Centar
  {
    start: { lat: 45.2701, lng: 19.8617 },
    end: { lat: 45.2549, lng: 19.8463 },
  },

  // 8. Centar → Spens
  {
    start: { lat: 45.2562, lng: 19.8468 },
    end: { lat: 45.2476, lng: 19.8354 },
  },

  // 9. Spens → Liman II
  {
    start: { lat: 45.2483, lng: 19.8336 },
    end: { lat: 45.2451, lng: 19.8481 },
  },

  // 10. Liman IV → Telep
  {
    start: { lat: 45.2437, lng: 19.8572 },
    end: { lat: 45.2401, lng: 19.8094 },
  },

  // 11. Detelinara → Sajlovo
  {
    start: { lat: 45.2635, lng: 19.8162 },
    end: { lat: 45.2718, lng: 19.7789 },
  },

  // 12. Sajlovo → Klisa
  {
    start: { lat: 45.2726, lng: 19.7771 },
    end: { lat: 45.2796, lng: 19.8449 },
  },

  // 13. Klisa → Petrovaradin
  {
    start: { lat: 45.2807, lng: 19.8462 },
    end: { lat: 45.2471, lng: 19.8733 },
  },

  // 14. Petrovaradin → Liman III
  {
    start: { lat: 45.2478, lng: 19.8749 },
    end: { lat: 45.2431, lng: 19.8426 },
  },

  // 15. Liman II → Podbara
  {
    start: { lat: 45.2464, lng: 19.8473 },
    end: { lat: 45.2687, lng: 19.8584 },
  },

  // 16. Podbara → Novo Naselje
  {
    start: { lat: 45.2696, lng: 19.8621 },
    end: { lat: 45.2667, lng: 19.8038 },
  },

  // 17. Novo Naselje → Centar
  {
    start: { lat: 45.2659, lng: 19.8051 },
    end: { lat: 45.2551, lng: 19.8438 },
  },

  // 18. Centar → Sajlovo
  {
    start: { lat: 45.2546, lng: 19.8479 },
    end: { lat: 45.2712, lng: 19.7796 },
  },

  // 19. Telep → Detelinara
  {
    start: { lat: 45.2392, lng: 19.8088 },
    end: { lat: 45.2621, lng: 19.8156 },
  },

  // 20. Detelinara → Petrovaradin
  {
    start: { lat: 45.2639, lng: 19.8131 },
    end: { lat: 45.2469, lng: 19.8721 },
  },
];

export type RideRequestDto = {
  route: {
    points: RideRoutePointDto[];
  };
  schedule: {
    type: 'NOW' | 'LATER';
    startAt: string;
  };
  vehicleTypeId: number;
  preferences: {
    baby: boolean;
    pets: boolean;
  };
  linkedPassengers: string[];
  freeDriversSnapshot: {
    driverId: number;
    lat: number;
    lng: number;
  }[];
};

export type DriverSummaryDto = {
  id: number;
  firstName: string;
  lastName: string;
  pfpUrl?: string;
  status?: string;
};

export type RideRequestResponseDto = {
  requestId: number;
  status: 'ACCEPTED' | 'REJECTED' | 'PENDING';
  distanceKm: number;
  price: number;
  scheduledTime: string | null;
  driver: DriverSummaryDto | null;
  pickupLat: number | null;
  pickupLng: number | null;
};

@Injectable({ providedIn: 'root' })
export class RideApi {
  private readonly baseUrl = 'http://localhost:8080/api/rides';
  private readonly driversBaseUrl = 'http://localhost:8080/api/drivers';

  constructor(private http: HttpClient) {}

  createRideRequest(payload: RideRequestDto): Observable<RideRequestResponseDto> {
    return this.http.post<RideRequestResponseDto>(`${this.baseUrl}/requests`, payload);
  }

  getActiveDrivers(): Observable<DriverSummaryDto[]> {
    return this.http.get<DriverSummaryDto[]>(`${this.driversBaseUrl}/active`);
  }
}
