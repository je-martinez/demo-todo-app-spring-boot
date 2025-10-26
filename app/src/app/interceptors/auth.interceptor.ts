import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthStore } from '../store/auth.store';

// Paths that should NOT have the Authorization token appended
const EXCLUDED_PATHS = ['/api/auth/login', '/api/auth/register', '/api/auth/refresh'];

/**
 * Check if the request URL matches any of the excluded paths
 */
const shouldExcludeRequest = (url: string): boolean => {
  return EXCLUDED_PATHS.some(path => url.includes(path));
};

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authStore = inject(AuthStore);
  const accessToken = authStore.accessToken();

  // Skip adding token if URL is in the exclusion list
  if (shouldExcludeRequest(req.url)) {
    return next(req);
  }

  // If there's a token, add it to the request headers
  if (accessToken) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
    });
    return next(cloned);
  }

  return next(req);
};
