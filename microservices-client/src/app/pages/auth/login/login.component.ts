import { Component } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../../core/services/auth-service';
import {FormsModule} from '@angular/forms';
import {NgIf} from '@angular/common';
import {LoginRequest} from '../../../core/models/loginRequest.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  imports: [
    FormsModule,
    NgIf,
    RouterLink
  ],
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

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
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error(err);
        alert('Invalid credentials or server error');
      }
    });
  }

}
