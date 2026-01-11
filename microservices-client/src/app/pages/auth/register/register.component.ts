import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import {Router, RouterLink} from '@angular/router';

import {AuthService} from '../../../core/services/auth-service';
import {RegisterRequest} from '../../../core/models/RegisterRequest.model';
import {RegisterResponse} from '../../../core/models/RegisterResponse.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, NgIf, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {

  registerRequest: RegisterRequest = {
    firstName: '',
    lastName: '',
    email: '',
    password: ''
  };

  showPassword = false;
  acceptTerms = false;
  errorMessage: string | null = null;
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  register(): void {
    this.errorMessage = null;

    if (!this.acceptTerms) {
      this.errorMessage = 'Please accept the Terms of Service';
      return;
    }

    if (this.isFormInvalid()) {
      this.errorMessage = 'Please fill all fields';
      return;
    }

    this.loading = true;

    this.authService.register(this.registerRequest).subscribe({
      next: (response: RegisterResponse) => {
        console.log(response.message);
        this.loading = false;

        // Redirection vers login après succès
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Registration failed';
      }
    });
  }

  private isFormInvalid(): boolean {
    const { firstName, lastName, email, password } = this.registerRequest;
    return !firstName || !lastName || !email || !password;
  }
}
