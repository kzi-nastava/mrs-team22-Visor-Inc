import { Component, inject, OnInit, signal, effect, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService } from '../../shared/websocket/ChatService';
import { AuthenticationService } from '../../shared/service/authentication-service';
import { toSignal } from '@angular/core/rxjs-interop';
import { HttpClient } from '@angular/common/http';

interface Message {
  text: string;
  sender: 'admin' | 'user';
  timestamp: Date;
}

@Component({
  selector: 'app-user-chat',
  imports: [CommonModule, FormsModule],
  templateUrl: './user-chat.html',
  styleUrl: './user-chat.css'
})
export class UserChat implements OnInit {
  private chatService = inject(ChatService);
  private authService = inject(AuthenticationService);
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);

  user = toSignal(this.authService.activeUser$);
  
  isExpanded = false;
  newMessage = '';
  
  messages = signal<Message[]>([]);

  constructor() {
    effect(() => {
      const currentUser = this.user();
      if (currentUser && currentUser.email) {
        this.setupChat(currentUser.email);
      }
    });
  }

  ngOnInit() {}

  private setupChat(email: string) {
    this.http.get<any[]>(`http://localhost:8080/api/chat/history/${email}`)
      .subscribe(history => {
        const mappedMessages: Message[] = history.map(m => ({
          text: m.content,
          sender: m.senderEmail === 'admin' ? 'admin' : 'user',
          timestamp: new Date(m.timestamp)
        }));
        this.messages.set(mappedMessages);
        this.cdr.detectChanges();
      });

    this.chatService.subscribeToMessages(email);
    
    this.chatService.messages$.subscribe(incomingMsg => {

      if (!incomingMsg) return;
      
      const currentUser = this.user();
      if (incomingMsg.recipientEmail === currentUser?.email || incomingMsg.senderEmail === currentUser?.email) {
        const newMsg: Message = {
          text: incomingMsg.content,
          sender: incomingMsg.senderEmail === 'admin' ? 'admin' : 'user',
          timestamp: new Date(incomingMsg.timestamp)
        };

        this.messages.update(prev => [...prev, newMsg]);
        this.cdr.detectChanges();
      }
    });
  }

  toggleChat() {
    this.isExpanded = !this.isExpanded;
  }

  sendMessage() {
    const currentUser = this.user();
    if (this.newMessage.trim() && currentUser) {
      const payload = {
        senderEmail: currentUser.email,
        recipientEmail: 'admin',
        content: this.newMessage
      };

      this.chatService.sendMessage(payload);
      this.newMessage = '';
    }
  }
}