import {Component} from '@angular/core';
import {Map} from '../../shared/map/map';
import {Dropdown} from '../../shared/dropdown/dropdown';
import {ValueInputString} from '../../shared/value-input/value-input-string/value-input-string';
import {MatButton} from '@angular/material/button';
import {RouterLink} from '@angular/router';
import {Header} from '../../shared/header/header';
import {Footer} from '../../shared/footer/footer';

export const ROUTE_HOME = 'home';

@Component({
  selector: 'app-home',
  imports: [Map, Dropdown, ValueInputString, MatButton, RouterLink, Header, Footer],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  public vehicleOptions = [
    { label: 'Standard', value: 100 },
    { label: 'Luxury', value: 200 },
    { label: 'Van', value: 150 },
  ];

  public selectedVehicle = 100;

  public timeOptions = [
    { label: 'Now', value: 'now' },
    { label: 'Later', value: 'later' },
  ];

  public selectedTime = 'now';
}
