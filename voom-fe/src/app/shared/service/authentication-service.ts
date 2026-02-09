import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import {BehaviorSubject, catchError, EMPTY, map, Observable, of, switchMap} from 'rxjs';
import {TokenDto, User} from '../rest/authentication/authentication.model';
import {jwtDecode, JwtPayload} from 'jwt-decode';
import ApiService from '../rest/api-service';
import {HttpClient} from '@angular/common/http';
import {isPlatformBrowser} from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {

  private readonly REFRESH_TOKEN = "VOOM_REFRESH_TOKEN";

  private _activeUser$ = new BehaviorSubject<User | null>(null);
  private _isReady$ = new BehaviorSubject<boolean>(false);

  private refreshToken: string | null = null;
  private tokenDto: TokenDto | null = null;

  constructor(private apiService: ApiService) {
    this.refreshToken = localStorage.getItem(this.REFRESH_TOKEN) ?? null;
    if (this.isValid(this.refreshToken)) {
      this.apiService.authenticationApi.refreshToken(this.refreshToken ?? '').pipe(
        map(response => response.data),
        catchError(() => {
          this.logout();
          this._isReady$.next(true);
          return of(null);
        }),
      ).subscribe(token => {
        if (!token) {
          this._isReady$.next(true);
          this.logout();
          return;
        }

        this.initiateAuthenticatedState(token);
      });
    } else {
      this._isReady$.next(true);
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
    this.tokenDto = response;
    this._isReady$.next(true);
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

  public get isReady$() {
    return this._isReady$.asObservable();
  }

  public get currentUserValue(): User | null {
    return this._activeUser$.value;
  }

  public get accessToken() {
    if (this.tokenDto && this.isValid(this.tokenDto.accessToken)) {
      return of(this.tokenDto.accessToken);
    } else {
      return this.apiService.authenticationApi.refreshToken(this.refreshToken ?? '').pipe(
        map(response => response.data),
        catchError(() => {
          this.logout();
          return EMPTY;
        }),
        switchMap((token) => {
          if (!token) {
            this.logout();
            return EMPTY;
          }

          this.tokenDto = token;

          return of(token.accessToken);
        })
      );
    }
  }
}
