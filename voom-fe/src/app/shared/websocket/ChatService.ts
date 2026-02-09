import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ChatService {
  private stompClient: Client;
  private messageSubject = new BehaviorSubject<any>(null);
  public messages$ = this.messageSubject.asObservable();

  constructor() {
    this.stompClient = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      onConnect: () => {
        console.log('STOMP Connected');
      },
      onStompError: (frame) => {
        console.error('STOMP Error', frame);
      }
    });
    this.stompClient.activate();
  }

  subscribeToMessages(email: string) {
    if (this.stompClient.connected) {
      this.doSubscribe(email);
    } else {
      this.stompClient.onConnect = () => this.doSubscribe(email);
    }
  }

  private doSubscribe(email: string) {
    this.stompClient.subscribe(`/topic/messages/${email}`, (msg) => {
      this.messageSubject.next(JSON.parse(msg.body));
    });
  }

  sendMessage(msg: any) {
    if (!this.stompClient.connected) {
      console.error("Cannot send message: Not connected");
      return;
    }
    this.stompClient.publish({
      destination: '/app/chat.sendMessage',
      body: JSON.stringify(msg)
    });
  }
}