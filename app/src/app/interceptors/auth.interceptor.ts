import {
  HttpInterceptorFn,
  HttpErrorResponse,
  HttpStatusCode,
  HttpEvent,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthStore } from '../store/auth.store';
import { AuthApi } from '@app/api/auth-api';
import { StorageService } from '@app/services';
import { decodeJwt } from '@app/utils';
import { UserJWTDecoded } from '@app/types';
import { BehaviorSubject, filter, Observable, switchMap, take, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

// Paths that should NOT have the Authorization token appended
const EXCLUDED_PATHS = ['/api/auth/login', '/api/auth/register', '/api/auth/refresh'];

/**
 * Check if the request URL matches any of the excluded paths
 */
const shouldExcludeRequest = (url: string): boolean => {
  return EXCLUDED_PATHS.some(path => url.includes(path));
};

/**
 * Check if the request is a refresh token request
 */
const isRefreshTokenRequest = (url: string): boolean => {
  return url.includes('/api/auth/refresh');
};

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req, next): Observable<HttpEvent<any>> => {
  const authStore = inject(AuthStore);
  const authApi = inject(AuthApi);
  const storageService = inject(StorageService);
  const accessToken = authStore.accessToken();

  // Skip adding token if URL is in the exclusion list
  if (shouldExcludeRequest(req.url)) {
    return next(req);
  }

  // Add token to request
  const addToken = (token: string | null) => {
    if (!token) {
      return req;
    }
    return req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  };

  return next(addToken(accessToken)).pipe(
    catchError((error: HttpErrorResponse) => {
      // Only handle 401 Unauthorized errors
      if (error.status !== HttpStatusCode.Unauthorized) {
        return throwError(() => error);
      }

      // If it's a refresh token request and it fails, don't retry
      if (isRefreshTokenRequest(req.url)) {
        // Logout user on refresh token failure
        authStore.logout();
        return throwError(() => error);
      }

      // If we're not refreshing, start the refresh process
      if (!isRefreshing) {
        isRefreshing = true;
        refreshTokenSubject.next(null);

        const refreshToken = authStore.refreshToken();

        if (!refreshToken) {
          isRefreshing = false;
          authStore.logout();
          return throwError(() => error);
        }

        // Call the refresh token API directly
        return authApi.refreshToken(refreshToken).pipe(
          switchMap(response => {
            const decodedToken = decodeJwt(response.accessToken) as UserJWTDecoded;

            // Store updated session data in localStorage
            const sessionExpiry = decodedToken.exp * 1000;
            storageService.storeSession({
              accessToken: response.accessToken,
              refreshToken: response.refreshToken,
              userData: decodedToken,
              sessionExpiry,
            });

            // Update store state
            authStore.setTokens(response.accessToken, response.refreshToken);

            // Emit new token and complete the refresh
            refreshTokenSubject.next(response.accessToken);
            isRefreshing = false;

            // Retry the original request with the new token
            return next(addToken(response.accessToken));
          }),
          catchError(refreshError => {
            isRefreshing = false;
            refreshTokenSubject.next(null);
            authStore.logout();
            return throwError(() => refreshError);
          })
        );
      } else {
        // We're already refreshing, wait for the new token
        return refreshTokenSubject.pipe(
          filter(token => token !== null),
          take(1),
          switchMap(token => {
            if (token) {
              return next(addToken(token));
            }
            return throwError(() => error);
          })
        );
      }
    })
  );
};
