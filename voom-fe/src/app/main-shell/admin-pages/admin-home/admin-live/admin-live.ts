import {Component, signal} from '@angular/core';
import {MatCard} from '@angular/material/card';
import {AdminSupportChat} from './admin-support-chat/admin-support-chat';
import {Map} from '../../../../shared/map/map';

export const ROUTE_ADMIN_LIVE = "live"

@Component({
  selector: 'app-admin-live',
  imports: [
    MatCard,
    AdminSupportChat,
    Map
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
