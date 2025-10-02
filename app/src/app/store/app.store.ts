import { computed, inject } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { catchError, map, of, switchMap, tap, timer } from 'rxjs';
import { AuthStore } from './auth.store';

export interface AppState {
  isInitializing: boolean;
  isInitialized: boolean;
  initializationError: string | null;
}

const initialState: AppState = {
  isInitializing: false,
  isInitialized: false,
  initializationError: null,
};

export const AppStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed(store => ({
    isReady: computed(() => store.isInitialized() && !store.isInitializing()),
    hasError: computed(() => !!store.initializationError()),
  })),
  withMethods((store, authStore = inject(AuthStore)) => ({
    // Initialize the application
    initializeApp: rxMethod<void>(c$ =>
      c$.pipe(
        tap(() => {
          console.log('Initializing app...');
          patchState(store, {
            isInitializing: true,
            isInitialized: false,
            initializationError: null,
          });
        }),
        switchMap(() => {
          try {
            // Try to restore session from localStorage
            const sessionRestored = authStore.initializeFromStorage();

            if (sessionRestored) {
              console.log('Session restored from localStorage');
              // Check if token needs refresh
              authStore.ensureValidToken();
            } else {
              console.log('No valid session found in localStorage');
            }

            console.log('App initialization completed');

            // Add a small delay for better UX, then complete initialization
            return timer(500).pipe(
              tap(() => {
                patchState(store, {
                  isInitializing: false,
                  isInitialized: true,
                });
                console.log('App is now ready');
              })
            );
          } catch (error) {
            console.error('Error during app initialization:', error);

            // Clear any corrupted session data
            authStore.clearSession();

            patchState(store, {
              isInitializing: false,
              isInitialized: false,
              initializationError: error instanceof Error ? error.message : 'Initialization failed',
            });

            return of(null);
          }
        }),
        catchError(error => {
          console.error('Error during app initialization:', error);

          // Clear any corrupted session data
          authStore.clearSession();

          patchState(store, {
            isInitializing: false,
            isInitialized: false,
            initializationError: error instanceof Error ? error.message : 'Initialization failed',
          });

          return of(null);
        })
      )
    ),

    // Reset initialization state
    resetInitialization: () => {
      patchState(store, {
        isInitializing: false,
        isInitialized: false,
        initializationError: null,
      });
    },

    // Clear initialization error
    clearError: () => {
      patchState(store, { initializationError: null });
    },
  }))
);
