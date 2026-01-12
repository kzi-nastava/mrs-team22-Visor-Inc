import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';

@Injectable({ providedIn: 'root' })
export class DriverSimulationWsService {
  private client!: Client;

  connect(
    onRoute: (msg: any) => void,
    onScheduledRide: (msg: any) => void,
    onDriverAssigned?: (msg: any) => void
  ) {
    this.client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      reconnectDelay: 5000,
      debug: (msg) => console.log('[STOMP]', msg),
    });

    this.client.onConnect = () => {
      console.log('[WS] connected');

      this.client.subscribe('/topic/route', (message) => {
        onRoute(JSON.parse(message.body));
      });

      this.client.subscribe('/topic/scheduled-rides', (message) => {
        onScheduledRide(JSON.parse(message.body));
      });

      this.client.subscribe('/topic/driver-assigned', (message) => {
        const payload = JSON.parse(message.body);
        console.log('[WS] Driver assigned:', payload);
        onDriverAssigned?.(payload);
      });
    };

    this.client.onWebSocketError = (err) => {
      console.error('[WS ERROR]', err);
    };

    this.client.onStompError = (frame) => {
      console.error('[STOMP ERROR]', frame);
    };

    this.client.activate();
  }

  requestRoute(payload: any) {
    this.client.publish({
      destination: '/app/route',
      body: JSON.stringify(payload),
    });
  }
}
