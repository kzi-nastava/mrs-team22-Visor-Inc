import {Component, Inject, inject} from '@angular/core';
import ApiService from '../rest/api-service';
import {Map} from '../map/map';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {RideHistoryDto} from '../rest/ride/ride.model';
import {ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-activity-map',
  imports: [
    Map,
    ReactiveFormsModule
  ],
  templateUrl: './activity-map.html',
  styleUrl: './activity-map.css',
})
export class ActivityMap {

  apiService = inject(ApiService);

  constructor(private dialogRef: MatDialogRef<ActivityMap>, @Inject(MAT_DIALOG_DATA) public data: RideHistoryDto) {}

  protected transformRoutePoints() {
    return this.data.rideRoute.routePoints.map(routePoint => {
      return {
        lat: routePoint.lat,
        lng: routePoint.lng,
        type: routePoint.pointType,
        orderIndex: routePoint.orderIndex,
      }
    });
  }
}
