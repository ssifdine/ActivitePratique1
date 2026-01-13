import { CanActivateFn } from '@angular/router';

export const authorizationGuardGuard: CanActivateFn = (route, state) => {
  return true;
};
