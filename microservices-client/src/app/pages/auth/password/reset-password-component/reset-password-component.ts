import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import {AuthService} from '../../../../core/services/auth-service';
import {MessageResponse} from '../../../../core/models/MessageResponse.model';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  templateUrl: './reset-password-component.html',
  imports: [
    FormsModule,
    NgIf,
    RouterLink
  ],
  styleUrls: ['./reset-password-component.css']
})
export class ResetPasswordComponent {
  newPassword: string = '';
  confirmPassword: string = '';
  passwordChanged: boolean = false;
  token: string = ''; // token depuis l'URL
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    // Récupérer le token de l'URL
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || '';
      if (!this.token) {
        this.errorMessage = 'Invalid or missing reset token.';
      }
    });
  }

  resetPassword() {
    event?.preventDefault();

    if (!this.newPassword || !this.confirmPassword) {
      this.errorMessage = 'Please fill in both fields.';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match!';
      return;
    }

    if (!this.token) {
      this.errorMessage = 'Invalid or missing reset token.';
      return;
    }

    // Appel à l'API pour reset password
    this.authService.resetPassword(this.token, this.newPassword)
      .subscribe({
        next: (res: MessageResponse) => {
          this.passwordChanged = true;
          this.errorMessage = '';
          // Réinitialiser les champs
          this.newPassword = '';
          this.confirmPassword = '';
        },
        error: (err) => {
          console.error(err);
          this.errorMessage = err.error?.message || 'Failed to reset password.';
        }
      });
  }
}
