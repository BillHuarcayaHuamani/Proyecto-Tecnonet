import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter, withViewTransitions } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './services/auth.interceptor';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(BrowserModule, BrowserAnimationsModule),
    provideRouter(
      routes,
      withViewTransitions()
    ),
    provideHttpClient(
      withInterceptors([
        (req, next) => {
          const apiReq = req.clone({ 
            url: req.url.startsWith('/api') ? `http://localhost:8080${req.url}` : req.url 
          });
          return next(apiReq);
        },
        authInterceptor
      ])
    ),
  ],
};