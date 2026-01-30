import {Component, Inject, inject} from '@angular/core';
import ApiService from '../rest/api-service';
import {Map} from '../map/map';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {RideHistoryDto} from '../rest/ride/ride.model';

@Component({
  selector: 'app-activity-map',
  imports: [
    Map
  ],
  templateUrl: './activity-map.html',
  styleUrl: './activity-map.css',
})
export class ActivityMap {

  apiService = inject(ApiService);

  constructor(private dialogRef: MatDialogRef<ActivityMap>, @Inject(MAT_DIALOG_DATA) public data: RideHistoryDto) {}



}
