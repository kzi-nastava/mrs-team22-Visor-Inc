import {Injectable} from '@angular/core';
import {BehaviorSubject, catchError, map, Observable, of} from 'rxjs';
import {TokenDto, User} from '../rest/authentication/authentication.model';
import {jwtDecode, JwtPayload} from 'jwt-decode';
import ApiService from '../rest/api-service';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {

  private readonly REFRESH_TOKEN = "VOOM_REFRESH_TOKEN";

  private _activeUser$ = new BehaviorSubject<User | null>(null);

  private refreshToken: string | null = null;

  constructor(private apiService: ApiService) {
    this.refreshToken = localStorage.getItem(this.REFRESH_TOKEN) ?? null;

    if (this.isValid(this.refreshToken)) {
      this.apiService.authenticationApi.refreshToken(this.refreshToken ?? '').pipe(
        map(response => response.data),
        catchError(() => {
          this.logout();
          return of(null);
        }),
      ).subscribe(token => {
        if (!token) {
          this.logout();
          return;
        }

        this.initiateAuthenticatedState(token);
      });
    } else {
      this.logout();
    }
  }

  public isAuthenticated(): Observable<boolean> {
    return of(this.isValid(this.refreshToken));
  }

  public setAuthentication(response: TokenDto) {
    this.refreshToken = response.refreshToken;
    localStorage.setItem(this.REFRESH_TOKEN, response.refreshToken);
    this.initiateAuthenticatedState(response);
  }

  private initiateAuthenticatedState(response: TokenDto) {
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

  public get activeUser$() {
    return this._activeUser$;
  }

  public get accessToken() {
    return of('');
  }
}
