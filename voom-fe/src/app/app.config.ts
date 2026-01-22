import {
  ApplicationConfig,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection, provideZonelessChangeDetection,
} from '@angular/core';
import {provideRouter, withDebugTracing} from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import { provideNativeDateAdapter } from '@angular/material/core';
import {HttpClient, provideHttpClient, withInterceptors} from '@angular/common/http';
import {authenticationInterceptor} from './shared/rest/authentication-interceptor';
import {VoomApiService} from './shared/rest/voom-api-service';
import {ApiClient} from './shared/rest/api-client';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideClientHydration(withEventReplay()),
    provideNativeDateAdapter(),
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: {
        subscriptSizing: 'dynamic',
        appearance: 'outline',
      },
    },
    {
      provide: ApiClient,
      useClass: VoomApiService,
      deps: [HttpClient],
    },
    provideHttpClient(withInterceptors([authenticationInterceptor])),
  ],
};
