import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import {
  heroCheckCircle,
  heroClipboardDocumentList,
  heroUser
} from '@ng-icons/heroicons/outline';

@Component({
  selector: 'app-sign-in-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NgIconComponent],
  providers: [provideIcons({ heroCheckCircle, heroClipboardDocumentList, heroUser })],
  templateUrl: './sign-in-layout.component.html',
  styleUrls: ['./sign-in-layout.component.css']
})
export class SignInLayoutComponent {
  // Layout component for sign-in pages
}
