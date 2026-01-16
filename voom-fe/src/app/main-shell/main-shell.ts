import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Footer} from '../shared/footer/footer';
import {Header} from '../shared/header/header';

export const ROUTE_MAIN_SHELL = "";

@Component({
  selector: 'app-main-shell',
  imports: [
    RouterOutlet,
    Header,
    Footer,
    Header
  ],
  templateUrl: './main-shell.html',
  styleUrl: './main-shell.css',
})
export class MainShell {

}
