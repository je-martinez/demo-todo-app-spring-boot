import { Injectable, inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthFacade } from '@app/store/facades/auth.facade';

@Injectable({
  providedIn: 'root',
})
export class GuestGuardService {
  private readonly authFacade = inject(AuthFacade);
  private readonly router = inject(Router);

  canActivate(): boolean {
    if (this.authFacade.isLoggedIn()) {
      // Redirect to your-tasks page if already authenticated
      this.router.navigate(['/your-tasks']);
      return false;
    }

    return true;
  }
}

export const guestGuard: CanActivateFn = () => {
  return inject(GuestGuardService).canActivate();
};
