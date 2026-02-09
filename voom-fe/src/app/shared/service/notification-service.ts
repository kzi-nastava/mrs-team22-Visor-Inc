import { Injectable } from '@angular/core';

export interface NotificationOptions {
  title: string;
  message: string;
  type: 'success' | 'error' | 'warning' | 'info' | 'panic';
  duration?: number; // milliseconds, 0 = no auto-dismiss
  sound?: boolean;
  vibration?: boolean;
  action?: {
    label: string;
    callback: () => void;
  };
}

export interface Notification extends NotificationOptions {
  id: string;
  timestamp: Date;
}

@Injectable({
  providedIn: 'root',
})
export class NotificationService {

  private hasPermission = false;

  constructor() {
    this.checkNotificationPermission();
  }

  private async checkNotificationPermission(): Promise<void> {
    if ('Notification' in window) {
      if (Notification.permission === 'granted') {
        this.hasPermission = true;
      } else if (Notification.permission !== 'denied') {
        const permission = await Notification.requestPermission();
        this.hasPermission = permission === 'granted';
      }
    }
  }

  private generateId(): string {
    return `notification-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  }

  async requestPermission(): Promise<boolean> {
    if ('Notification' in window && Notification.permission === 'default') {
      const permission = await Notification.requestPermission();
      this.hasPermission = permission === 'granted';
      return this.hasPermission;
    }
    return this.hasPermission;
  }

  show(options: NotificationOptions): string {
      const notification: Notification = {
        ...options,
        id: this.generateId(),
        timestamp: new Date(),
        duration: options.duration ?? 5000,
        sound: options.sound ?? false,
        vibration: options.vibration ?? false,
      };

      // Show browser notification if permitted and not in focus
      if (this.hasPermission && document.hidden) {
        this.showBrowserNotification(notification);
      }

      return notification.id;
  }

  private showBrowserNotification(notification: Notification): void {
    if (!this.hasPermission) return;

    const options: NotificationOptions & { icon?: string; badge?: string } = {
      title: notification.title,
      message: notification.message,
      type: notification.type,
      icon: '/assets/images/logo.png',
      badge: '/assets/icons/location.png',
    };

    const browserNotification = new Notification(notification.title, {
      body: notification.message,
      icon: options.icon,
      badge: options.badge,
      tag: notification.type,
      requireInteraction: notification.type === 'panic', // Panic alerts don't auto-dismiss
    });

    browserNotification.onclick = () => {
      window.focus();
      if (notification.action) {
        notification.action.callback();
      }
      browserNotification.close();
    };
  }

}
