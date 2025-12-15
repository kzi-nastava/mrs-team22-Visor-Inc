import { Component, signal } from '@angular/core';
import { Header } from './header/header';
import { Map } from './map/map';
import { RideForm } from './ride-form/ride-form';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [Header, Map, RideForm, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('voom-fe');
}
