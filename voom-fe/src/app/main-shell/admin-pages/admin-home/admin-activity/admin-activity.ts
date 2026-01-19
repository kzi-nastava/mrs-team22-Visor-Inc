import {Component} from '@angular/core';
import {MatIcon} from '@angular/material/icon';
import {ValueInputString} from '../../../../shared/value-input/value-input-string/value-input-string';

export const ROUTE_ADMIN_ACTIVITY = "activity";

@Component({
  selector: 'app-admin-activity',
  imports: [
    MatIcon,
    ValueInputString
  ],
  templateUrl: './admin-activity.html',
  styleUrl: './admin-activity.css',
})
export class AdminActivity {

}
