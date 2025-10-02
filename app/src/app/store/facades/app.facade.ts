import { Injectable, inject } from '@angular/core';
import { AppStore } from '../app.store';

@Injectable({
  providedIn: 'root',
})
export class AppFacade {
  private readonly appStore = inject(AppStore);

  // State selectors
  readonly isInitializing = this.appStore.isInitializing;
  readonly isInitialized = this.appStore.isInitialized;
  readonly isReady = this.appStore.isReady;
  readonly hasError = this.appStore.hasError;
  readonly initializationError = this.appStore.initializationError;

  // Actions
  initializeApp() {
    this.appStore.initializeApp();
  }

  resetInitialization() {
    this.appStore.resetInitialization();
  }

  clearError() {
    this.appStore.clearError();
  }
}
