import {Component} from '@angular/core';
import {ValueInputString} from '../../shared/value-input/value-input-string/value-input-string';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MatButton} from '@angular/material/button';

export const ROUTE_LOGIN = 'login';

@Component({
  selector: 'app-login',
  imports: [
    ValueInputString,
    MatButton
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  form = new FormGroup({
    email: new FormControl<string>('', [Validators.required, Validators.email]),
    password: new FormControl<string>('', [Validators.required]),
  });

}
