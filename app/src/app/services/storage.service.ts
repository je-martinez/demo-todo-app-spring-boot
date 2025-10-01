import { Injectable } from '@angular/core';

export interface StorageKeys {
  readonly ACCESS_TOKEN: 'auth_access_token';
  readonly REFRESH_TOKEN: 'auth_refresh_token';
  readonly USER_DATA: 'auth_user_data';
  readonly SESSION_EXPIRY: 'auth_session_expiry';
}

export interface SessionData {
  accessToken: string;
  refreshToken: string;
  userData: any;
  sessionExpiry: number;
}

@Injectable({
  providedIn: 'root',
})
export class StorageService {
  private readonly STORAGE_KEYS: StorageKeys = {
    ACCESS_TOKEN: 'auth_access_token',
    REFRESH_TOKEN: 'auth_refresh_token',
    USER_DATA: 'auth_user_data',
    SESSION_EXPIRY: 'auth_session_expiry',
  } as const;

  private readonly isLocalStorageAvailable: boolean;

  constructor() {
    this.isLocalStorageAvailable = this.checkLocalStorageAvailability();
  }

  /**
   * Check if localStorage is available in the current environment
   */
  private checkLocalStorageAvailability(): boolean {
    try {
      if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
        return false;
      }

      // Test localStorage access
      const testKey = '__localStorage_test__';
      localStorage.setItem(testKey, 'test');
      localStorage.removeItem(testKey);
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Set a value in localStorage with error handling
   */
  setItem(key: string, value: string): boolean {
    if (!this.isLocalStorageAvailable) {
      console.warn('localStorage is not available');
      return false;
    }

    try {
      localStorage.setItem(key, value);
      return true;
    } catch (error) {
      console.error('Failed to set localStorage item:', error);
      return false;
    }
  }

  /**
   * Get a value from localStorage with error handling
   */
  getItem(key: string): string | null {
    if (!this.isLocalStorageAvailable) {
      return null;
    }

    try {
      return localStorage.getItem(key);
    } catch (error) {
      console.error('Failed to get localStorage item:', error);
      return null;
    }
  }

  /**
   * Remove an item from localStorage
   */
  removeItem(key: string): boolean {
    if (!this.isLocalStorageAvailable) {
      return false;
    }

    try {
      localStorage.removeItem(key);
      return true;
    } catch (error) {
      console.error('Failed to remove localStorage item:', error);
      return false;
    }
  }

  /**
   * Clear all localStorage items
   */
  clear(): boolean {
    if (!this.isLocalStorageAvailable) {
      return false;
    }

    try {
      localStorage.clear();
      return true;
    } catch (error) {
      console.error('Failed to clear localStorage:', error);
      return false;
    }
  }

  /**
   * Store session data (tokens, user data, expiry)
   */
  storeSession(sessionData: SessionData): boolean {
    const success =
      this.setItem(this.STORAGE_KEYS.ACCESS_TOKEN, sessionData.accessToken) &&
      this.setItem(this.STORAGE_KEYS.REFRESH_TOKEN, sessionData.refreshToken) &&
      this.setItem(this.STORAGE_KEYS.USER_DATA, JSON.stringify(sessionData.userData)) &&
      this.setItem(this.STORAGE_KEYS.SESSION_EXPIRY, sessionData.sessionExpiry.toString());

    if (success) {
      console.log('Session data stored successfully');
    } else {
      console.error('Failed to store session data');
    }

    return success;
  }

  /**
   * Retrieve session data from localStorage
   */
  getSession(): SessionData | null {
    const accessToken = this.getItem(this.STORAGE_KEYS.ACCESS_TOKEN);
    const refreshToken = this.getItem(this.STORAGE_KEYS.REFRESH_TOKEN);
    const userDataStr = this.getItem(this.STORAGE_KEYS.USER_DATA);
    const sessionExpiryStr = this.getItem(this.STORAGE_KEYS.SESSION_EXPIRY);

    if (!accessToken || !refreshToken || !userDataStr || !sessionExpiryStr) {
      return null;
    }

    try {
      const userData = JSON.parse(userDataStr);
      const sessionExpiry = parseInt(sessionExpiryStr, 10);

      return {
        accessToken,
        refreshToken,
        userData,
        sessionExpiry,
      };
    } catch (error) {
      console.error('Failed to parse session data:', error);
      this.clearSession();
      return null;
    }
  }

  /**
   * Check if session exists and is valid
   */
  hasValidSession(): boolean {
    const session = this.getSession();
    if (!session) {
      return false;
    }

    // Check if session has expired
    const now = Date.now();
    return session.sessionExpiry > now;
  }

  /**
   * Clear session data from localStorage
   */
  clearSession(): boolean {
    const success =
      this.removeItem(this.STORAGE_KEYS.ACCESS_TOKEN) &&
      this.removeItem(this.STORAGE_KEYS.REFRESH_TOKEN) &&
      this.removeItem(this.STORAGE_KEYS.USER_DATA) &&
      this.removeItem(this.STORAGE_KEYS.SESSION_EXPIRY);

    if (success) {
      console.log('Session data cleared successfully');
    } else {
      console.error('Failed to clear session data');
    }

    return success;
  }

  /**
   * Store only tokens (for refresh scenarios)
   */
  storeTokens(accessToken: string, refreshToken: string): boolean {
    return (
      this.setItem(this.STORAGE_KEYS.ACCESS_TOKEN, accessToken) &&
      this.setItem(this.STORAGE_KEYS.REFRESH_TOKEN, refreshToken)
    );
  }

  /**
   * Get stored tokens
   */
  getTokens(): { accessToken: string | null; refreshToken: string | null } {
    return {
      accessToken: this.getItem(this.STORAGE_KEYS.ACCESS_TOKEN),
      refreshToken: this.getItem(this.STORAGE_KEYS.REFRESH_TOKEN),
    };
  }

  /**
   * Store user data
   */
  storeUserData(userData: any): boolean {
    return this.setItem(this.STORAGE_KEYS.USER_DATA, JSON.stringify(userData));
  }

  /**
   * Get stored user data
   */
  getUserData(): any | null {
    const userDataStr = this.getItem(this.STORAGE_KEYS.USER_DATA);
    if (!userDataStr) {
      return null;
    }

    try {
      return JSON.parse(userDataStr);
    } catch (error) {
      console.error('Failed to parse user data:', error);
      return null;
    }
  }

  /**
   * Update session expiry time
   */
  updateSessionExpiry(expiryTime: number): boolean {
    return this.setItem(this.STORAGE_KEYS.SESSION_EXPIRY, expiryTime.toString());
  }

  /**
   * Get session expiry time
   */
  getSessionExpiry(): number | null {
    const expiryStr = this.getItem(this.STORAGE_KEYS.SESSION_EXPIRY);
    if (!expiryStr) {
      return null;
    }

    try {
      return parseInt(expiryStr, 10);
    } catch (error) {
      console.error('Failed to parse session expiry:', error);
      return null;
    }
  }

  /**
   * Check if localStorage is available
   */
  isAvailable(): boolean {
    return this.isLocalStorageAvailable;
  }

  /**
   * Get storage usage information (if available)
   */
  getStorageInfo(): { used: number; available: number } | null {
    if (!this.isLocalStorageAvailable || !('storage' in navigator)) {
      return null;
    }

    try {
      const storage = (navigator as any).storage;
      if (storage && storage.estimate) {
        return {
          used: storage.estimate().usage || 0,
          available: storage.estimate().quota || 0,
        };
      }
    } catch (error) {
      console.error('Failed to get storage info:', error);
    }

    return null;
  }
}
