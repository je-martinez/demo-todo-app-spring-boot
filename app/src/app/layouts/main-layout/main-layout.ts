import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { AuthFacade } from '@app/store/facades';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './main-layout.html',
})
export class MainLayout {
  private readonly authFacade = inject(AuthFacade);

  // State selectors
  readonly user = this.authFacade.user;
  readonly userEmail = this.authFacade.userEmail;
  readonly isLoggedIn = this.authFacade.isLoggedIn;

  // Actions
  logout() {
    this.authFacade.logout();
  }
}
