import { Component } from '@angular/core';
import {NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-forgot-password-component',
  imports: [
    NgIf,
    FormsModule,
    RouterLink
  ],
  templateUrl: './forgot-password-component.html',
  styleUrl: './forgot-password-component.css',
})
export class ForgotPasswordComponent {

  email = '';
  emailSent = false;

  sendResetLink(): void {
    if (!this.email) {
      alert('Please enter your email address');
      return;
    }

    // Appel API pour envoyer le lien
    // this.authService.forgotPassword(this.email).subscribe(...)

    // Simuler l'envoi
    this.emailSent = true;
  }

  resendEmail(): void {
    // Renvoyer l'email
    console.log('Resending email to:', this.email);
    // this.authService.forgotPassword(this.email).subscribe(...)
  }
}
