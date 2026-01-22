import {Component} from '@angular/core';
import {Header} from '../shared/header/header';
import {RouterOutlet} from '@angular/router';

@Component({
  selector: 'app-main-shell',
  imports: [
    Header,
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
