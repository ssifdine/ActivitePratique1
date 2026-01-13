import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../core/services/auth-service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  private isRefreshing = false;
  private refreshTokenSubject = new BehaviorSubject<string | null>(null);

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    const token = this.authService.getAccessToken();
    let authReq = req;

    // ➕ Ajouter Authorization header (sauf auth endpoints)
    if (token && !this.isAuthRequest(req)) {
      authReq = this.addToken(req, token);
    }

    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {

        // 🔐 Token expiré → refresh
        if (
          error.status === 401 &&
          error.error?.message === 'Token has expired' &&
          !this.isAuthRequest(req)
        ) {
          return this.handle401Error(authReq, next);
        }

        // 🔐 Token invalide / refresh expiré
        if (error.status === 401) {
          this.authService.logout();
          this.router.navigate(['/login']);
        }

        // ⛔ Accès refusé
        if (error.status === 403) {
          this.router.navigate(['/not-authorized']);
        }

        return throwError(() => error);
      })
    );
  }

  // ====================== PRIVATE METHODS ======================

  private addToken(req: HttpRequest<any>, token: string) {
    return req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  private handle401Error(req: HttpRequest<any>, next: HttpHandler) {

    // ❌ Pas de refresh token → logout
    if (!this.authService.getRefreshToken()) {
      this.authService.logout();
      this.router.navigate(['/login']);
      return throwError(() => new Error('No refresh token'));
    }

    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      return this.authService.refreshToken().pipe(
        switchMap(res => {
          this.isRefreshing = false;

          this.authService.saveAccessToken(res.accessToken);
          this.refreshTokenSubject.next(res.accessToken);

          return next.handle(this.addToken(req, res.accessToken));
        }),
        catchError(err => {
          this.isRefreshing = false;
          this.authService.logout();
          this.router.navigate(['/login']);
          return throwError(() => err);
        })
      );
    }

    // ⏳ Attendre le refresh en cours
    return this.refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap(token => next.handle(this.addToken(req, token!)))
    );
  }

  private isAuthRequest(req: HttpRequest<any>): boolean {
    return req.url.includes('/login')
      || req.url.includes('/refresh')
      || req.url.includes('/register');
  }
}
