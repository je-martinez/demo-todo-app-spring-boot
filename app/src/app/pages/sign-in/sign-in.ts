import { Component, signal, inject, computed, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import {
  heroEnvelope,
  heroLockClosed,
  heroEye,
  heroEyeSlash,
  heroXMark,
} from '@ng-icons/heroicons/outline';
import { LoadingSpinnerComponent } from '@components/loading-spinner/loading-spinner';
import { ErrorDisplayComponent } from '@components/error-display/error-display';
import { AuthFacade } from '@app/store/facades/auth.facade';

@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgIconComponent,
    LoadingSpinnerComponent,
    ErrorDisplayComponent,
  ],
  providers: [provideIcons({ heroEnvelope, heroLockClosed, heroEye, heroEyeSlash, heroXMark })],
  templateUrl: './sign-in.html',
})
export class SignInComponent {
  private readonly authFacade = inject(AuthFacade);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  signInForm: FormGroup;
  showPassword = signal(false);

  // Use auth facade state instead of local loading state
  readonly isLoading = this.authFacade.isLoading;
  readonly error = this.authFacade.error;
  readonly isAuthenticated = this.authFacade.isAuthenticated;

  constructor() {
    // Get email from query parameters
    const emailFromQuery = this.route.snapshot.queryParams['email'] || '';

    this.signInForm = this.fb.group({
      email: [emailFromQuery, [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(9), this.passwordPatternValidator]],
    });

    // Effect to handle navigation when login is successful
    effect(() => {
      if (this.isAuthenticated() && !this.isLoading()) {
        this.router.navigate(['/']);
      }
    });
  }

  passwordPatternValidator(control: any) {
    const password = control.value;
    if (!password) return null;

    // Regex: at least 9 characters, at least one digit, one uppercase, one lowercase
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{9,}$/;

    if (!passwordRegex.test(password)) {
      return { passwordPattern: true };
    }

    return null;
  }

  onSubmit() {
    if (this.signInForm.valid) {
      const { email, password } = this.signInForm.value;

      // Clear any existing errors before attempting login
      this.clearError();

      // Trigger the login
      this.authFacade.login(email, password);
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(this.signInForm.controls).forEach(key => {
        this.signInForm.get(key)?.markAsTouched();
      });
    }
  }

  onRegisterClick() {
    // Clear any errors before navigating
    this.clearError();
    // Navigate to sign-up page
    this.router.navigate(['/sign-up']);
  }

  togglePasswordVisibility() {
    this.showPassword.set(!this.showPassword());
  }

  get emailError() {
    const email = this.signInForm.get('email');
    if (email?.touched && email?.errors) {
      if (email.errors['required']) return 'Email is required';
      if (email.errors['email']) return 'Please enter a valid email';
    }
    return null;
  }

  get passwordError() {
    const password = this.signInForm.get('password');
    if (password?.touched && password?.errors) {
      if (password.errors['required']) return 'Password is required';
      if (password.errors['minlength']) return 'Password must be at least 9 characters';
      if (password.errors['passwordPattern'])
        return 'Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character';
    }
    return null;
  }

  // Error handling methods
  get hasError() {
    return !!this.error();
  }

  clearError() {
    this.authFacade.clearError();
  }

  // Clear error when form changes
  onFormChange() {
    if (this.hasError) {
      this.clearError();
    }
  }
}
