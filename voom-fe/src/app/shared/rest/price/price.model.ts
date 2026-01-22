export interface CreatePriceDto {
  vehicleTypeId: number;
  pricePerKm: number;
}

export interface PriceDto {
  priceId: number;
  vehicleTypeId: number;
  pricePerKm: number;
}
