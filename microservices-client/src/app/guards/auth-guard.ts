import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../core/services/auth-service';

export const authGuard: CanActivateFn = () => {

  const authService = inject(AuthService);
  const router = inject(Router);

  // 🔐 Pas de token → login
  if (!authService.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  // ✅ Token présent → autorisé
  return true;
};
