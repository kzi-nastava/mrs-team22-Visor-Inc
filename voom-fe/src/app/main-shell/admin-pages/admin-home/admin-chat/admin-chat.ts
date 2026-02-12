import { ChangeDetectorRef, Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService } from '../../../../shared/websocket/ChatService';
import { HttpClient } from '@angular/common/http';

export const ROUTE_ADMIN_CHAT = "chat";


interface Message {
  text: string;
  sender: 'admin' | 'user';
  timestamp: Date;
}

interface ChatUser {
  senderFirstName: string;
  senderLastName: string;
  email: string;
  profilePic: string | null;
  messages: Message[];
}

@Component({
  selector: 'app-admin-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-chat.html',
  styleUrl: './admin-chat.css',
})
export class AdminChat {
  private chatService = inject(ChatService);
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);
  users = signal<ChatUser[]>([]);
  selectedUser = signal<ChatUser | null>(null)

  newMessage: string = '';

  selectUser(user: ChatUser) {
    this.selectedUser.set(user);
    if (true) {
      this.http.get<any[]>(`http://localhost:8080/api/chat/history/${user.email}`)
        .subscribe(history => {
          user.messages = history.map(m => ({
            text: m.content,
            sender: m.senderEmail === 'admin' ? 'admin' : 'user',
            timestamp: new Date(m.timestamp)
          }));
          this.cdr.detectChanges();
        });
    }
  }

  constructor() {
    this.loadInitialConversations();
    this.listenForMessages();
  }

  loadInitialConversations() {
    this.http.get<ChatUser[]>('http://localhost:8080/api/chat/conversations')
      .subscribe(data => {
        this.users.set(data);
        this.users().forEach(user => {
          user.messages = [];
          user.profilePic = user.profilePic || 'assets/icons/free driver.png';
        });
      });
  }

  sendMessage() {
    if (this.newMessage.trim() && this.selectedUser) {
      const chatMessage = {
        senderEmail: 'admin',
        recipientEmail: this.selectedUser()?.email,
        content: this.newMessage
      };
      this.chatService.sendMessage(chatMessage);
      this.newMessage = '';
    }
  }

  listenForMessages() {
    this.chatService.subscribeToMessages('admin');

    this.chatService.messages$.subscribe(incomingMsg => {
      if (!incomingMsg) return;

      const partnerEmail = incomingMsg.senderEmail === 'admin' 
        ? incomingMsg.recipientEmail 
        : incomingMsg.senderEmail;

      let user = this.users().find(u => u.email === partnerEmail);

      if (!user) {
        const newUser : ChatUser = {
          senderFirstName: incomingMsg.senderFirstName, senderLastName: incomingMsg.senderLastName,
          email: partnerEmail, profilePic: 'assets/icons/free driver.png',
          messages: []
        };
        this.users.update(currentUsers => [newUser, ...currentUsers]);
      }

      user?.messages.push({
        text: incomingMsg.content,
        sender: incomingMsg.senderEmail === 'admin' ? 'admin' : 'user',
        timestamp: new Date(incomingMsg.timestamp)
      });

      this.cdr.detectChanges();
    });
  }
}