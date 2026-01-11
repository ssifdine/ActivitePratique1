import { Component } from '@angular/core';
import { Router } from '@angular/router';
import {AuthService} from '../../../core/services/auth-service';
import {FormsModule} from '@angular/forms';
import {NgIf} from '@angular/common';
import {LoginRequest} from '../../../core/models/loginRequest.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.html',
  imports: [
    FormsModule,
    NgIf
  ],
  styleUrls: ['./login.css']
})
export class Login {

  loginRequest: LoginRequest = {
    email: '',
    password: ''
  };

  showPassword = false;
  rememberMe = false;

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    if (!this.loginRequest.email || !this.loginRequest.password) {
      alert('Please fill all fields');
      return;
    }

    this.authService.login(this.loginRequest).subscribe({
      next: (res) => {
        console.log('Logged in successfully', res);
        // Redirige vers la page principale (dashboard ou customers)
        this.router.navigate(['/customers']);
      },
      error: (err) => {
        console.error(err);
        alert('Invalid credentials or server error');
      }
    });
  }
}
