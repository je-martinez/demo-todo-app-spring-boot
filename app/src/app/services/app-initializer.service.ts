import { Injectable, inject } from '@angular/core';
import { AuthFacade } from '@app/store/facades/auth.facade';

@Injectable({
  providedIn: 'root',
})
export class AppInitializerService {
  private readonly authFacade = inject(AuthFacade);

  /**
   * Initialize the application by restoring session from localStorage
   */
  async initializeApp(): Promise<void> {
    try {
      console.log('Initializing app...');

      // Try to restore session from localStorage
      const sessionRestored = this.authFacade.initializeFromStorage();

      if (sessionRestored) {
        console.log('Session restored from localStorage');

        // Check if token needs refresh
        this.authFacade.ensureValidToken();
      } else {
        console.log('No valid session found in localStorage');
      }

      console.log('App initialization completed');
    } catch (error) {
      console.error('Error during app initialization:', error);

      // Clear any corrupted session data
      this.authFacade.clearSession();
    }
  }
}

/**
 * Factory function for APP_INITIALIZER
 */
export function initializeAppFactory(initializer: AppInitializerService) {
  return () => initializer.initializeApp();
}
