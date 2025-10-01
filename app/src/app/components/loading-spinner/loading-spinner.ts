import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loading-spinner.html',
  styleUrl: './loading-spinner.css',
})
export class LoadingSpinnerComponent {
  @Input() size: 'sm' | 'md' | 'lg' = 'md';
  @Input() color: string = 'text-white';
  @Input() showText: boolean = false;
  @Input() text: string = 'Loading...';

  get sizeClasses(): string {
    switch (this.size) {
      case 'sm':
        return 'h-4 w-4';
      case 'lg':
        return 'h-8 w-8';
      default: // md
        return 'h-5 w-5';
    }
  }

  get textSizeClasses(): string {
    switch (this.size) {
      case 'sm':
        return 'text-sm';
      case 'lg':
        return 'text-lg';
      default: // md
        return 'text-base';
    }
  }
}
