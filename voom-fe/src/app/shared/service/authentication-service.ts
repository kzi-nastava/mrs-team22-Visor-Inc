import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {SignInResponse, User} from '../../core/rest/authentication/authentication.model';
import {jwtDecode, JwtPayload} from 'jwt-decode';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {

  private readonly REFRESH_TOKEN = "VOOM_REFRESH_TOKEN";

  private _activeUser$ = new BehaviorSubject<User | null>(null);

  private refreshToken: string | null = null;

  constructor() {
    this.refreshToken = localStorage.getItem(this.REFRESH_TOKEN) ?? null;

    if (this.isValid(this.refreshToken)) {
      //TODO refresh token
    } else {
      this.logout();
    }
  }

  public setAuthentication(response: SignInResponse) {
    this.refreshToken = response.refreshToken;
    localStorage.setItem(this.REFRESH_TOKEN, response.refreshToken);
    this.initiateAuthenticatedState(response);
  }

  public get accessToken(): Observable<string> {
    return of('')
  }

  private initiateAuthenticatedState(response: SignInResponse) {
    this._activeUser$.next(response.user);
  }

  public logout() {
    this.refreshToken = null;
    localStorage.removeItem(this.REFRESH_TOKEN);
  }

  private isValid(token: string | null | undefined): boolean {
    if (!token) {
      return false;
    }

    const payload = jwtDecode<JwtPayload>(token);
    return (payload.exp ?? 0) * 1000 >= new Date().valueOf();
  }

}
