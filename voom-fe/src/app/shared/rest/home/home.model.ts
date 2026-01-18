export type RideReportRequestDto = {
  message: string;
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