export type RideReportRequestDto = {
  message: string;
};
export type RatingRequestDto = {
  driverRating: number;
  vehicleRating: number;
  comment?: string;
};


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
  orderIndex: number;
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
    address: string;
  }[];
};

export type RideResponseDto = {
  id: number;
  status: 'CREATED' | 'ONGOING' | 'FINISHED' | 'CANCELLED';
  startedAt: string;
  finishedAt: string | null;
  driverName: string;
  passengerName: string;
  passengerNames: string[];
  driverId: number;
  startAddress: string;
  destinationAddress: string;
};


export type DriverAssignedDto = {
  driverId: number;
  rideId: number;
  route: {
    lat: number;
    lng: number;
    order: number;
    type: 'PICKUP' | 'STOP' | 'DROPOFF';
    address: string;
  }[];
};

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

export interface RideRequestResponseDto {
  requestId: number;
  status: 'ACCEPTED' | 'REJECTED' | 'PENDING' | 'SCHEDULED' | 'CANCELLED';
  distanceKm: number;
  price: number;
  scheduledTime: string | null;
  driver: DriverSummaryDto | null;
  pickupLat: number | null;
  pickupLng: number | null;
};

export type RideStatus = 'SCHEDULED' | 'ONGOING' | 'CANCELLED' | 'FINISHED' | 'PANIC';


export interface RoutePointDto {
  address: string;
  lat: number;
  lng: number;
  orderIndex: number | null;
  type: RoutePointType;
}

export interface OngoingRideDto {
  rideId: number;
  status: RideStatus;
  routePoints: RoutePointDto[];
  driverId: number;
}


export type RideRequestStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED';
export type ScheduleType = 'NOW' | 'LATER';
export type UserStatus = 'PENDING' | 'ACTIVE' | 'BLOCKED';

export interface RoutePoint {
  orderIndex: number;
  lat: number;
  lng: number;
  address: string;
  pointType: RoutePointType;
}

export interface RideRoute {
  id: number;
  totalDistanceKm: number;
  routePoints: RoutePoint[];
}

export interface UserRole {
  id: number;
  name: string;
}

export interface Person {
  id: number;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  address: string;
  profilePicture?: string;
}

export interface User {
  id: number;
  email: string;
  person: Person;
  userStatus: UserStatus;
  userRole: UserRole;
}

export interface VehicleType {
  id: number;
  name: string;
  pricePerKm: number;
}

export interface RideRequest {
  id: number;
  creator: User;
  rideRoute: RideRoute;
  status: RideRequestStatus;
  scheduleType: ScheduleType;
  scheduledTime?: string;
  vehicleType: VehicleType;
  babyTransport: boolean;
  petTransport: boolean;
  calculatedPrice: number;
  linkedPassengerEmails: string[];
  cancelledBy?: User;
}

export interface RideHistoryDto {
  id: number;
  status: RideStatus;
  rideRequest: RideRequest;
  rideRoute: RideRoute;
  startedAt: string;
  finishedAt: string;
  passengers: User[];
  cancelledBy?: User;
}

export interface RideCancellationDto {
  userId: number;
  message: string;
}

export interface RideStopDto {
  userId: number;
  route: RoutePointDto[];
  timestamp: string;
}

export interface RidePanicDto {
  userId: number,
}

export interface StartRideDto {
  routePoints: RideRoutePointDto[];
}
