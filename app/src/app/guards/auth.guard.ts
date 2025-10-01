import { Injectable, inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthFacade } from '@app/store/facades/auth.facade';

@Injectable({
  providedIn: 'root',
})
export class AuthGuardService {
  private readonly authFacade = inject(AuthFacade);
  private readonly router = inject(Router);

  canActivate(): boolean {
    if (this.authFacade.isLoggedIn()) {
      return true;
    }

    // Redirect to sign-in page if not authenticated
    this.router.navigate(['/sign-in']);
    return false;
  }
}

export const authGuard: CanActivateFn = () => {
  return inject(AuthGuardService).canActivate();
};
