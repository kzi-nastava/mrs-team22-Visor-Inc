import { Component, signal } from '@angular/core';
import { Header } from './header/header';
import { Map } from './map/map';
import { RideForm } from './ride-form/ride-form';

@Component({
  selector: 'app-root',
  imports: [Header, Map, RideForm],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('voom-fe');
}
