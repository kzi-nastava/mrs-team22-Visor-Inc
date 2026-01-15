import {Component, signal} from '@angular/core';
import {Map} from '../../../shared/map/map';
import {MatCard} from '@angular/material/card';
import {MatDialog} from '@angular/material/dialog';
import {AdminSupportChat} from './admin-support-chat/admin-support-chat';

export const ROUTE_ADMIN_LIVE = "live"

@Component({
  selector: 'app-admin-live',
  imports: [
    Map,
    MatCard,
    AdminSupportChat
  ],
  templateUrl: './admin-live.html',
  styleUrl: './admin-live.css',
})
export class AdminLive {

  isOpen = signal<boolean>(true);

  constructor() {
  }

  protected openSupportChat() {
    const isOpen = this.isOpen();
    this.isOpen.set(!isOpen);
  }
}
