import {Component} from '@angular/core';
import {Footer} from '../shared/footer/footer';
import {Header} from '../shared/header/header';
import {RouterOutlet} from '@angular/router';

export const ROUTE_MAIN_SHELL = "main";

@Component({
  selector: 'app-main-shell',
  imports: [
    Header,
    Footer,
    Header,
    RouterOutlet
  ],
  templateUrl: './main-shell.html',
  styleUrl: './main-shell.css',
})
export class MainShell {
  constructor() {
  }

}
