import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {Observable, tap} from 'rxjs';
import {AuthResponse} from '../models/AuthResponse.model';
import {LoginRequest} from '../models/loginRequest.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private apiUrl = 'http://localhost:8888/AUTH-SERVICE/api/auth';

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request)
      .pipe(
        tap(res => {
          localStorage.setItem('access_token', res.accessToken);
          localStorage.setItem('refresh_token', res.refreshToken);
          localStorage.setItem('role', res.role);
          localStorage.setItem('userId', res.userId);
          localStorage.setItem('email', res.email);
        })
      );
  }

  logout(): void {
    localStorage.clear();
  }

  getToken(): string | null {
    return localStorage.getItem('access_token');
  }

  getCurrentUser() {
    return {
      email: localStorage.getItem('email'),
      role: localStorage.getItem('role'),
      userId: localStorage.getItem('userId')
    };
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

}
