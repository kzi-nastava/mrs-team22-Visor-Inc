import {LatLng} from 'leaflet';


export interface RouteEstimateRequestDto {
  startPoint: LatLng,
  endPoint: LatLng,
}

export interface RouteEstimateResponseDto {
  duration: number,
  distance: number,
}
