import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { AppFacade } from '@app/store/facades';

@Component({
  selector: 'app-sign-in-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './sign-in-layout.html',
})
export class SignInLayout {
  private readonly appFacade = inject(AppFacade);
  public readonly isAppInitialized = this.appFacade.isReady;
}
