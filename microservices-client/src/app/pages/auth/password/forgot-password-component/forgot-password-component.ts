import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {AuthService} from '../../../../core/services/auth-service';

@Component({
  selector: 'app-forgot-password-component',
  standalone: true,
  imports: [NgIf, FormsModule, RouterLink],
  templateUrl: './forgot-password-component.html',
  styleUrl: './forgot-password-component.css',
})
export class ForgotPasswordComponent {

  email = '';
  emailSent = false;
  isLoading = false;
  errorMessage = '';

  constructor(private authService: AuthService) {}

  sendResetLink(): void {
    if (!this.email) {
      this.errorMessage = 'Veuillez entrer votre adresse email';
      return;
    }

    if (!this.isValidEmail(this.email)) {
      this.errorMessage = 'Veuillez entrer une adresse email valide';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.forgotPassword(this.email).subscribe({
      next: (response) => {
        console.log('Reset email response:', response);
        this.emailSent = true;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error sending reset email:', error);
        // Même en cas d'erreur, on affiche un message générique
        // pour ne pas révéler si l'email existe ou non
        this.emailSent = true;
        this.isLoading = false;
      }
    });
  }

  resendEmail(): void {
    this.emailSent = false;
    this.isLoading = false;
    this.errorMessage = '';
    // L'utilisateur peut renvoyer en cliquant à nouveau sur le bouton
  }

  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }
}
