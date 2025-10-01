import { Injectable, computed, inject } from '@angular/core';
import { AuthStore } from '../auth.store';
import { UserJWTDecoded } from '@app/types';

@Injectable({
  providedIn: 'root',
})
export class AuthFacade {
  private readonly authStore = inject(AuthStore);

  // State selectors - computed signals for reactive state access
  readonly user = this.authStore.user;
  readonly accessToken = this.authStore.accessToken;
  readonly refreshToken = this.authStore.refreshToken;
  readonly isLoading = this.authStore.isLoading;
  readonly error = this.authStore.error;

  // Computed selectors - derived state
  readonly isAuthenticated = this.authStore.isAuthenticated;
  readonly isTokenExpired = this.authStore.isTokenExpired;
  readonly userEmail = this.authStore.userEmail;

  // Computed helpers for common use cases
  readonly hasError = computed(() => !!this.error());
  readonly isLoggedIn = computed(() => this.isAuthenticated() && !this.isTokenExpired());
  readonly canRefreshToken = computed(() => !!this.refreshToken() && this.isTokenExpired());

  // Action methods - clean API for store actions
  login(email: string, password: string) {
    return this.authStore.login({ email, password });
  }

  register(email: string, password: string) {
    return this.authStore.register({ email, password });
  }

  logout() {
    this.authStore.logout();
  }

  refreshAuthToken() {
    return this.authStore.refreshToken();
  }

  clearError() {
    this.authStore.clearError();
  }

  setTokens(accessToken: string, refreshToken: string) {
    this.authStore.setTokens(accessToken, refreshToken);
  }

  ensureValidToken() {
    this.authStore.ensureValidToken();
  }

  // Helper methods for common patterns
  loginAndWait(email: string, password: string) {
    this.login(email, password);
    return this.isLoggedIn;
  }

  registerAndWait(email: string, password: string) {
    this.register(email, password);
    return true;
  }

  // Token management helpers
  getStoredTokens(): { accessToken: string | null; refreshToken: string | null } {
    return {
      accessToken: this.accessToken(),
      refreshToken: this.refreshToken(),
    };
  }

  // User info helpers
  getUserInfo(): UserJWTDecoded | null {
    return this.user();
  }

  getUserId(): string | null {
    return this.user()?.sub || null;
  }

  // Error handling helpers
  getErrorMessage(): string | null {
    return this.error();
  }

  hasSpecificError(errorMessage: string): boolean {
    return this.error()?.includes(errorMessage) || false;
  }

  // Loading state helpers
  isOperationInProgress(): boolean {
    return this.isLoading();
  }

  // Authentication flow helpers
  async performAuthenticatedAction<T>(action: () => Promise<T>): Promise<T | null> {
    if (!this.isLoggedIn()) {
      if (this.canRefreshToken()) {
        this.refreshAuthToken();
      } else {
        throw new Error('User not authenticated and cannot refresh token');
      }
    }

    if (this.isLoggedIn()) {
      return await action();
    }

    return null;
  }

  // State snapshot helpers (for non-reactive access)
  getAuthStateSnapshot() {
    return {
      user: this.user(),
      accessToken: this.accessToken(),
      refreshToken: this.refreshToken(),
      isLoading: this.isLoading(),
      error: this.error(),
      isAuthenticated: this.isAuthenticated(),
      isTokenExpired: this.isTokenExpired(),
      userEmail: this.userEmail(),
      isLoggedIn: this.isLoggedIn(),
      canRefreshToken: this.canRefreshToken(),
      hasError: this.hasError(),
    };
  }
}
