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

@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgIconComponent],
  providers: [provideIcons({ heroEnvelope, heroLockClosed, heroEye, heroEyeSlash })],
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css']
})
export class SignInComponent {
  signInForm: FormGroup;
  isLoading = signal(false);
  showPassword = signal(false);

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {
    this.signInForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit() {
    if (this.signInForm.valid) {
      this.isLoading.set(true);

      // Simulate API call
      setTimeout(() => {
        this.isLoading.set(false);
        // Navigate to main page or dashboard
        this.router.navigate(['/']);
      }, 1000);
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(this.signInForm.controls).forEach(key => {
        this.signInForm.get(key)?.markAsTouched();
      });
    }
  }

  onRegisterClick() {
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
      if (password.errors['minlength']) return 'Password must be at least 6 characters';
    }
    return null;
  }
}
