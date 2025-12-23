import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Header} from '../core/layout/header-kt1/header-kt1';
import {Footer} from '../core/layout/footer/footer';

@Component({
  selector: 'app-main-shell',
  imports: [
    RouterOutlet,
    Header,
    Footer
  ],
  templateUrl: './main-shell.html',
  styleUrl: './main-shell.css',
})
export class MainShell {

}
