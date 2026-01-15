import {Component} from '@angular/core';
import {MatCard, MatCardContent, MatCardFooter, MatCardTitle} from '@angular/material/card';
import {MatDivider} from '@angular/material/list';
import {ValueInputString} from '../../../../shared/value-input/value-input-string/value-input-string';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-admin-support-chat',
  imports: [
    MatCard,
    MatCardContent,
    MatDivider,
    MatCardFooter,
    ValueInputString,
    MatIcon,
    MatCardTitle
  ],
  templateUrl: './admin-support-chat.html',
  styleUrl: './admin-support-chat.css',
})
export class AdminSupportChat {

}
