export interface CreateVehicleDto {
  vehicleTypeId: number;
  driverId: number;
  year: number;
  model: string;
  licensePlate: string;
  babySeat: boolean;
  petFriendly: boolean;
  numberOfSeats: number;
}

export interface VehicleDto {
  id: number;
  driverId: number;
  vehicleTypeId: number;
  year: number;
  model: string;
  licensePlate: string;
  babySeat: boolean;
  petFriendly: boolean;
  numberOfSeats: number;
}
