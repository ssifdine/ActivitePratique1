import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

import { AuthResponse } from '../models/AuthResponse.model';
import { LoginRequest } from '../models/loginRequest.model';
import { RegisterRequest } from '../models/RegisterRequest.model';
import { RegisterResponse } from '../models/RegisterResponse.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private apiUrl = 'http://localhost:8888/AUTH-SERVICE/api/auth';

  constructor(private http: HttpClient, private router: Router) {}

  // ================= AUTH =================

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(res => this.saveTokens(res)),
      catchError(error => {
        console.error('Login failed:', error);
        return throwError(() => error);
      })
    );
  }

  register(request: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.apiUrl}/register`, request);
  }

  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      this.logout();
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http.post<AuthResponse>(`${this.apiUrl}/refresh`, { refreshToken }).pipe(
      tap(res => this.saveAccessToken(res.accessToken)),
      catchError(error => {
        console.error('Token refresh failed:', error);
        this.logout();
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      this.forceLogout();
      return;
    }
    this.http.post(`${this.apiUrl}/logout`, { refreshToken }).subscribe({
      next: () => this.forceLogout(),
      error: () => this.forceLogout()
    });
  }

  private forceLogout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  // ================= TOKEN STORAGE =================

  private saveTokens(res: AuthResponse): void {
    this.saveAccessToken(res.accessToken);
    localStorage.setItem('refreshToken', res.refreshToken);
    localStorage.setItem('role', res.role);
    localStorage.setItem('userId', res.userId);
    localStorage.setItem('email', res.email);
  }

  saveAccessToken(token: string): void {
    localStorage.setItem('accessToken', token);
  }

  getAccessToken(): string | null {
    return localStorage.getItem('accessToken');
  }

  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  // ================= USER =================

  getCurrentUser() {
    return {
      email: localStorage.getItem('email'),
      role: localStorage.getItem('role'),
      userId: localStorage.getItem('userId')
    };
  }

  isAuthenticated(): boolean {
    const token = this.getAccessToken();
    return !!token;
  }

  isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return Date.now() >= payload.exp * 1000;
    } catch {
      return true;
    }
  }

  // ================= ROLES =================

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  hasRole(role: string): boolean {
    return this.getRole() === role;
  }

  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  isUser(): boolean {
    return this.hasRole('USER');
  }
}
