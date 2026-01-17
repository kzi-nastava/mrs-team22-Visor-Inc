import {Component, inject} from '@angular/core';
import {AuthenticationService} from '../service/authentication-service';
import {toSignal} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-footer',
  imports: [],
  templateUrl: './footer.html',
  styleUrl: './footer.css',
})
export class Footer {

  private authenticationService = inject(AuthenticationService);

  isAuthenticated = toSignal(this.authenticationService.isAuthenticated());

  constructor() {
  }
}
