import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import {RidePanicDto, RideResponseDto} from '../rest/ride/ride.model';

@Injectable({ providedIn: 'root' })
export class DriverSimulationWsService {
  private client!: Client;

  connect(
    onRoute: (msg: any) => void,
    onScheduledRide: (msg: any) => void,
    onDriverAssigned?: (msg: any) => void,
    onDriverPosition?: (msg: any) => void,
    onRideChange?: (msg: RideResponseDto) => void,
    onRidePanic?: (msg: RideResponseDto) => void,
  ) {
    this.client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      reconnectDelay: 5000,
      // debug: (msg) => console.log('[STOMP]', msg),
    });

    this.client.onConnect = () => {
      // console.log('[WS] connected');

      this.client.subscribe('/topic/route', (message) => {
        onRoute(JSON.parse(message.body));
      });

      this.client.subscribe('/topic/scheduled-rides', (message) => {
        onScheduledRide(JSON.parse(message.body));
      });

      this.client.subscribe('/topic/driver-assigned', (message) => {
        const payload = JSON.parse(message.body);
        // console.log('[WS] Driver assigned:', payload);
        onDriverAssigned?.(payload);
      });

      this.client.subscribe('/topic/drivers-positions', (message) => {
        const payload = JSON.parse(message.body);
        onDriverPosition?.(payload);
      });

      this.client.subscribe("/topic/ride-changes", (message) => {
        const dto = JSON.parse(message.body);
        onRideChange?.(dto);
      });


      this.client.subscribe("/topic/ride-panic", (message) => {
        const dto = JSON.parse(message.body);
        onRidePanic?.(dto);
      })
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
