import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroXMark } from '@ng-icons/heroicons/outline';
import { ApiError } from '@app/store/auth.store';

@Component({
  selector: 'app-error-display',
  standalone: true,
  imports: [CommonModule, NgIconComponent],
  providers: [provideIcons({ heroXMark })],
  templateUrl: './error-display.html',
})
export class ErrorDisplayComponent {
  @Input() error: ApiError | null = null;
  @Input() showCloseButton: boolean = true;
  @Output() clearError = new EventEmitter<void>();

  get hasError(): boolean {
    return !!this.error;
  }

  get errorMessage(): string {
    return this.error?.message || '';
  }

  get errorList(): string[] {
    return this.error?.errors || [];
  }

  onClearError(): void {
    this.clearError.emit();
  }
}
