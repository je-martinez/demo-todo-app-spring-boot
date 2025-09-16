import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import {
  heroEnvelope,
  heroLockClosed,
  heroEye,
  heroEyeSlash
} from '@ng-icons/heroicons/outline';
import { LoadingSpinnerComponent } from '@components/loading-spinner/loading-spinner';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgIconComponent, LoadingSpinnerComponent],
  providers: [provideIcons({ heroEnvelope, heroLockClosed, heroEye, heroEyeSlash })],
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent {
  signUpForm: FormGroup;
  isLoading = signal(false);
  showPassword = signal(false);
  showConfirmPassword = signal(false);

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {
    this.signUpForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', [Validators.required]]
    }, { validators: this.passwordMatchValidator });
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
      this.isLoading.set(true);

      // Simulate API call
      setTimeout(() => {
        this.isLoading.set(false);
        // Navigate to main page or dashboard
        this.router.navigate(['/']);
      }, 1000);
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(this.signUpForm.controls).forEach(key => {
        this.signUpForm.get(key)?.markAsTouched();
      });
    }
  }

  onSignInClick() {
    // Navigate to sign-in page
    this.router.navigate(['/sign-in']);
  }

  togglePasswordVisibility() {
    this.showPassword.set(!this.showPassword());
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword.set(!this.showConfirmPassword());
  }

  get firstNameError() {
    const firstName = this.signUpForm.get('firstName');
    if (firstName?.touched && firstName?.errors) {
      if (firstName.errors['required']) return 'First name is required';
      if (firstName.errors['minlength']) return 'First name must be at least 2 characters';
    }
    return null;
  }

  get lastNameError() {
    const lastName = this.signUpForm.get('lastName');
    if (lastName?.touched && lastName?.errors) {
      if (lastName.errors['required']) return 'Last name is required';
      if (lastName.errors['minlength']) return 'Last name must be at least 2 characters';
    }
    return null;
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
      if (password.errors['minlength']) return 'Password must be at least 8 characters';
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
}
