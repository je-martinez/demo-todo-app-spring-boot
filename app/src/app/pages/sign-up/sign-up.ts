import { Component, signal, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
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
  selector: 'app-sign-up',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgIconComponent,
    LoadingSpinnerComponent,
    ErrorDisplayComponent,
  ],
  providers: [provideIcons({ heroEnvelope, heroLockClosed, heroEye, heroEyeSlash, heroXMark })],
  templateUrl: './sign-up.html',
})
export class SignUpComponent {
  private readonly authFacade = inject(AuthFacade);
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);

  signUpForm: FormGroup;
  showPassword = signal(false);
  showConfirmPassword = signal(false);

  // Use auth facade state instead of local loading state
  readonly isLoading = this.authFacade.isLoading;
  readonly error = this.authFacade.error;
  readonly redirectToSignIn = this.authFacade.redirectToSignIn;

  constructor() {
    this.signUpForm = this.fb.group(
      {
        email: ['', [Validators.required, Validators.email]],
        password: [
          '',
          [Validators.required, Validators.minLength(9), this.passwordPatternValidator],
        ],
        confirmPassword: ['', [Validators.required]],
      },
      { validators: this.passwordMatchValidator }
    );

    effect(() => {
      if (this.redirectToSignIn() && !this.isLoading()) {
        const email = this.signUpForm.get('email')?.value;
        if (email) {
          this.router.navigate(['/sign-in'], { queryParams: { email: email } });
          this.authFacade.clearRedirectToSignIn();
        }
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

  passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    }

    return null;
  }

  onSubmit() {
    if (this.signUpForm.valid) {
      const { email, password } = this.signUpForm.value;

      // Clear any existing errors before attempting registration
      this.clearError();

      // Trigger the registration
      this.authFacade.register(email, password);
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(this.signUpForm.controls).forEach(key => {
        this.signUpForm.get(key)?.markAsTouched();
      });
    }
  }

  onSignInClick() {
    // Clear any errors before navigating
    this.clearError();
    // Navigate to sign-in page
    this.router.navigate(['/sign-in']);
  }

  togglePasswordVisibility() {
    this.showPassword.set(!this.showPassword());
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword.set(!this.showConfirmPassword());
  }

  get emailError() {
    const email = this.signUpForm.get('email');
    if (email?.touched && email?.errors) {
      if (email.errors['required']) return 'Email is required';
      if (email.errors['email']) return 'Please enter a valid email';
    }
    return null;
  }

  get passwordError() {
    const password = this.signUpForm.get('password');
    if (password?.touched && password?.errors) {
      if (password.errors['required']) return 'Password is required';
      if (password.errors['minlength']) return 'Password must be at least 9 characters';
      if (password.errors['passwordPattern'])
        return 'Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character';
    }
    return null;
  }

  get confirmPasswordError() {
    const confirmPassword = this.signUpForm.get('confirmPassword');
    if (confirmPassword?.touched && confirmPassword?.errors) {
      if (confirmPassword.errors['required']) return 'Please confirm your password';
      if (confirmPassword.errors['passwordMismatch']) return 'Passwords do not match';
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
