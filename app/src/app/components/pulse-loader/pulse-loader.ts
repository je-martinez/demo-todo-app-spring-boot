import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export type PulseSize = 'sm' | 'md' | 'lg' | 'xl';
export type PulseVariant = 'dots' | 'bars' | 'circles' | 'squares' | 'waves';
export type PulseColor = 'primary' | 'secondary' | 'accent' | 'neutral' | 'white';

@Component({
  selector: 'app-pulse-loader',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pulse-loader.html',
  styleUrl: './pulse-loader.css',
})
export class PulseLoader {
  @Input() size: PulseSize = 'md';
  @Input() variant: PulseVariant = 'dots';
  @Input() color: PulseColor = 'primary';
  @Input() showText: boolean = true;
  @Input() text: string = 'Loading...';
  @Input() overlay: boolean = true;
  @Input() blur: boolean = false;
  @Input() fullScreen: boolean = true;

  get sizeClasses(): string {
    switch (this.size) {
      case 'sm':
        return 'text-sm';
      case 'lg':
        return 'text-lg';
      case 'xl':
        return 'text-xl';
      default: // md
        return 'text-base';
    }
  }

  get containerClasses(): string {
    const baseClasses = 'flex flex-col items-center justify-center';
    const fullScreenClasses = this.fullScreen ? 'fixed inset-0 z-50' : '';
    const overlayClasses = this.overlay ? 'bg-white/80 dark:bg-gray-900/80' : '';
    const blurClasses = this.blur ? 'backdrop-blur-sm' : '';

    return `${baseClasses} ${fullScreenClasses} ${overlayClasses} ${blurClasses}`.trim();
  }

  get colorClasses(): string {
    switch (this.color) {
      case 'primary':
        return 'text-blue-600';
      case 'secondary':
        return 'text-gray-600';
      case 'accent':
        return 'text-purple-600';
      case 'neutral':
        return 'text-gray-500';
      case 'white':
        return 'text-white';
      default:
        return 'text-blue-600';
    }
  }

  get pulseColorClasses(): string {
    switch (this.color) {
      case 'primary':
        return 'bg-blue-600';
      case 'secondary':
        return 'bg-gray-600';
      case 'accent':
        return 'bg-purple-600';
      case 'neutral':
        return 'bg-gray-500';
      case 'white':
        return 'bg-white';
      default:
        return 'bg-blue-600';
    }
  }

  get animationDelay(): string {
    switch (this.size) {
      case 'sm':
        return 'delay-75';
      case 'lg':
        return 'delay-150';
      case 'xl':
        return 'delay-200';
      default: // md
        return 'delay-100';
    }
  }
}
