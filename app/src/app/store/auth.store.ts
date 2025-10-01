import { computed, inject } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { catchError, map, of, switchMap, tap } from 'rxjs';
import { AuthApi } from '@app/api/auth-api';
import { UserJWTDecoded } from '@app/types';
import { decodeJwt } from '@app/utils';

export interface AuthState {
  user: UserJWTDecoded | null;
  accessToken: string | null;
  refreshToken: string | null;
  isLoading: boolean;
  error: string | null;
}

const initialState: AuthState = {
  user: null,
  accessToken: null,
  refreshToken: null,
  isLoading: false,
  error: null,
};

export const AuthStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed(store => ({
    isAuthenticated: computed(() => !!store.accessToken() && !!store.user()),
    isTokenExpired: computed(() => {
      const user = store.user();
      if (!user) return true;
      return Date.now() >= user.exp * 1000;
    }),
    userEmail: computed(() => store.user()?.sub || null),
  })),
  withMethods((store, authApi = inject(AuthApi)) => ({
    // Login action
    login: rxMethod<{ email: string; password: string }>(c$ =>
      c$.pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ email, password }) =>
          authApi.login(email, password).pipe(
            map(response => {
              const decodedToken = decodeJwt(response.accessToken) as UserJWTDecoded;
              patchState(store, {
                user: decodedToken,
                accessToken: response.accessToken,
                refreshToken: response.refreshToken,
                isLoading: false,
                error: null,
              });
              return response;
            }),
            catchError(error => {
              patchState(store, {
                isLoading: false,
                error: error.message || 'Login failed',
              });
              return of(null);
            })
          )
        )
      )
    ),

    // Register action
    register: rxMethod<{ email: string; password: string }>(c$ =>
      c$.pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ email, password }) =>
          authApi.register(email, password).pipe(
            map(response => {
              patchState(store, {
                isLoading: false,
                error: null,
              });
              return response;
            }),
            catchError(error => {
              patchState(store, {
                isLoading: false,
                error: error.message || 'Registration failed',
              });
              return of(null);
            })
          )
        )
      )
    ),

    // Refresh token action
    refreshToken: rxMethod<void>(c$ =>
      c$.pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(() => {
          const refreshToken = store.refreshToken();
          if (!refreshToken) {
            patchState(store, {
              isLoading: false,
              error: 'No refresh token available',
            });
            return of(null);
          }

          return authApi.refreshToken(refreshToken).pipe(
            map(response => {
              const decodedToken = decodeJwt(response.accessToken) as UserJWTDecoded;
              patchState(store, {
                user: decodedToken,
                accessToken: response.accessToken,
                refreshToken: response.refreshToken,
                isLoading: false,
                error: null,
              });
              return response;
            }),
            catchError(error => {
              patchState(store, {
                isLoading: false,
                error: error.message || 'Token refresh failed',
                user: null,
                accessToken: null,
                refreshToken: null,
              });
              return of(null);
            })
          );
        })
      )
    ),

    // Logout action
    logout: () => {
      patchState(store, {
        user: null,
        accessToken: null,
        refreshToken: null,
        isLoading: false,
        error: null,
      });
    },

    // Clear error action
    clearError: () => {
      patchState(store, { error: null });
    },

    // Set tokens manually (useful for initialization from localStorage)
    setTokens: (accessToken: string, refreshToken: string) => {
      try {
        const decodedToken = decodeJwt(accessToken) as UserJWTDecoded;
        patchState(store, {
          user: decodedToken,
          accessToken,
          refreshToken,
          error: null,
        });
      } catch (error) {
        patchState(store, {
          error: 'Invalid token format',
        });
      }
    },

    // Check if token needs refresh and refresh if necessary
    ensureValidToken: () => {
      const isExpired = store.isTokenExpired();
      if (isExpired && store.refreshToken()) {
        store.refreshToken();
      }
    },
  }))
);
