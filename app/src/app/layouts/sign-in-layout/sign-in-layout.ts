import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-sign-in-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './sign-in-layout.html',
})
export class SignInLayout {
  // Layout component for sign-in pages
}
