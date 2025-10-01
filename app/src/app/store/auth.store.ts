import { computed, inject } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { catchError, map, of, switchMap, tap } from 'rxjs';
import { AuthApi } from '@app/api/auth-api';
import { UserJWTDecoded } from '@app/types';
import { decodeJwt } from '@app/utils';
import { StorageService } from '@app/services';

export interface ApiError {
  message: string;
  errors: string[];
}

export interface AuthState {
  user: UserJWTDecoded | null;
  accessToken: string | null;
  refreshToken: string | null;
  isLoading: boolean;
  error: ApiError | null;
  redirectToSignIn: boolean;
}

const initialState: AuthState = {
  user: null,
  accessToken: null,
  refreshToken: null,
  isLoading: false,
  error: null,
  redirectToSignIn: false,
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
  withMethods((store, authApi = inject(AuthApi), storageService = inject(StorageService)) => ({
    // Login action
    login: rxMethod<{ email: string; password: string }>(c$ =>
      c$.pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(({ email, password }) =>
          authApi.login(email, password).pipe(
            map(response => {
              const decodedToken = decodeJwt(response.accessToken) as UserJWTDecoded;

              // Store session data in localStorage
              const sessionExpiry = decodedToken.exp * 1000; // Convert to milliseconds
              storageService.storeSession({
                accessToken: response.accessToken,
                refreshToken: response.refreshToken,
                userData: decodedToken,
                sessionExpiry,
              });

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
              let parsedError: ApiError;
              try {
                // Try to parse the error response as structured error
                const errorResponse = error.error;
                if (errorResponse && errorResponse.message) {
                  // Handle case with message and errors array
                  if (Array.isArray(errorResponse.errors)) {
                    parsedError = {
                      message: errorResponse.message,
                      errors: errorResponse.errors,
                    };
                  } else {
                    // Handle case with message but no errors array
                    parsedError = {
                      message: errorResponse.message,
                      errors: [errorResponse.message],
                    };
                  }
                } else {
                  // Fallback to simple error message
                  parsedError = {
                    message: error.message || 'Login failed',
                    errors: [error.message || 'Login failed'],
                  };
                }
              } catch {
                // If parsing fails, create a simple error
                parsedError = {
                  message: error.message || 'Login failed',
                  errors: [error.message || 'Login failed'],
                };
              }

              patchState(store, {
                isLoading: false,
                error: parsedError,
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
                redirectToSignIn: true,
              });
              return response;
            }),
            catchError(error => {
              let parsedError: ApiError;
              try {
                // Try to parse the error response as structured error
                const errorResponse = error.error;
                if (errorResponse && errorResponse.message) {
                  // Handle case with message and errors array
                  if (Array.isArray(errorResponse.errors)) {
                    parsedError = {
                      message: errorResponse.message,
                      errors: errorResponse.errors,
                    };
                  } else {
                    // Handle case with message but no errors array
                    parsedError = {
                      message: errorResponse.message,
                      errors: [errorResponse.message],
                    };
                  }
                } else {
                  // Fallback to simple error message
                  parsedError = {
                    message: error.message || 'Registration failed',
                    errors: [error.message || 'Registration failed'],
                  };
                }
              } catch {
                // If parsing fails, create a simple error
                parsedError = {
                  message: error.message || 'Registration failed',
                  errors: [error.message || 'Registration failed'],
                };
              }

              patchState(store, {
                isLoading: false,
                error: parsedError,
              });
              return of(null);
            })
          )
        )
      )
    ),

    // Refresh token action
    refreshAuthToken: rxMethod<void>(c$ =>
      c$.pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(() => {
          const refreshToken = store.refreshToken();
          if (!refreshToken) {
            patchState(store, {
              isLoading: false,
              error: {
                message: 'No refresh token available',
                errors: ['No refresh token available'],
              },
            });
            return of(null);
          }

          return authApi.refreshToken(refreshToken).pipe(
            map(response => {
              const decodedToken = decodeJwt(response.accessToken) as UserJWTDecoded;

              // Store updated session data in localStorage
              const sessionExpiry = decodedToken.exp * 1000; // Convert to milliseconds
              storageService.storeSession({
                accessToken: response.accessToken,
                refreshToken: response.refreshToken,
                userData: decodedToken,
                sessionExpiry,
              });

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
              let parsedError: ApiError;
              try {
                // Try to parse the error response as structured error
                const errorResponse = error.error;
                if (errorResponse && errorResponse.message) {
                  // Handle case with message and errors array
                  if (Array.isArray(errorResponse.errors)) {
                    parsedError = {
                      message: errorResponse.message,
                      errors: errorResponse.errors,
                    };
                  } else {
                    // Handle case with message but no errors array
                    parsedError = {
                      message: errorResponse.message,
                      errors: [errorResponse.message],
                    };
                  }
                } else {
                  // Fallback to simple error message
                  parsedError = {
                    message: error.message || 'Token refresh failed',
                    errors: [error.message || 'Token refresh failed'],
                  };
                }
              } catch {
                // If parsing fails, create a simple error
                parsedError = {
                  message: error.message || 'Token refresh failed',
                  errors: [error.message || 'Token refresh failed'],
                };
              }

              patchState(store, {
                isLoading: false,
                error: parsedError,
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
      // Clear session data from localStorage
      storageService.clearSession();

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

    // Clear redirectToSignIn action
    clearRedirectToSignIn: () => {
      patchState(store, { redirectToSignIn: false });
    },

    // Set tokens manually (useful for initialization from localStorage)
    setTokens: (accessToken: string, refreshToken: string) => {
      try {
        const decodedToken = decodeJwt(accessToken) as UserJWTDecoded;

        // Store session data in localStorage
        const sessionExpiry = decodedToken.exp * 1000; // Convert to milliseconds
        storageService.storeSession({
          accessToken,
          refreshToken,
          userData: decodedToken,
          sessionExpiry,
        });

        patchState(store, {
          user: decodedToken,
          accessToken,
          refreshToken,
          error: null,
        });
      } catch (error) {
        patchState(store, {
          error: {
            message: 'Invalid token format',
            errors: ['Invalid token format'],
          },
        });
      }
    },
  })),
  withMethods((store, storageService = inject(StorageService)) => ({
    // Check if token needs refresh and refresh if necessary
    ensureValidToken: () => {
      const isExpired = store.isTokenExpired();
      if (isExpired && store.refreshToken()) {
        // Trigger refresh by calling the rxMethod
        // Note: This will trigger the refresh asynchronously
        store.refreshAuthToken();
      }
    },

    // Initialize store from localStorage
    initializeFromStorage: () => {
      const sessionData = storageService.getSession();
      if (sessionData && storageService.hasValidSession()) {
        // Check if token is still valid (not expired)
        const now = Date.now();
        if (sessionData.sessionExpiry > now) {
          patchState(store, {
            user: sessionData.userData,
            accessToken: sessionData.accessToken,
            refreshToken: sessionData.refreshToken,
            error: null,
          });
          return true;
        } else {
          // Session expired, clear it
          storageService.clearSession();
        }
      }
      return false;
    },

    // Clear session and localStorage
    clearSession: () => {
      storageService.clearSession();
      patchState(store, {
        user: null,
        accessToken: null,
        refreshToken: null,
        isLoading: false,
        error: null,
      });
    },
  }))
);
