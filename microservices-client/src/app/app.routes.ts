import { Routes } from '@angular/router';

import { CustomerList } from './pages/customers/customer-list/customer-list';
import { CustomerForm } from './pages/customers/customer-form/customer-form';
import { CustomerEdit } from './pages/customers/customer-edit/customer-edit';

import { ProductList } from './pages/products/product-list/product-list';
import { ProductForm } from './pages/products/product-form/product-form';

import { BillList } from './pages/bills/bill-list/bill-list';
import { BillForm } from './pages/bills/bill-form/bill-form';
import { BillDetails } from './pages/bills/bill-details/bill-details';

import { DashboardComponent } from './pages/dashboard/dashboard-component/dashboard-component';

import { LoginComponent } from './pages/auth/login/login.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { NotAuthorizedComponent } from './pages/not-authorized/not-authorized.component';

import { authGuard } from './guards/auth-guard';
import {authorizationGuardGuard} from './guards/authorization-guard-guard';
import {ForgotPasswordComponent} from './pages/auth/password/forgot-password-component/forgot-password-component';
import {ResetPasswordComponent} from './pages/auth/password/reset-password-component/reset-password-component';

export const routes: Routes = [

  /* ================= PUBLIC ================= */
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'not-authorized', component: NotAuthorizedComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },

  /* ================= PROTECTED ================= */
  {
    path: '',
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },

      // Customers
      { path: 'customers', component: CustomerList },
      { path: 'customers/new', component: CustomerForm },
      { path: 'customers/:id/edit', component: CustomerEdit },

      // Products
      { path: 'products', component: ProductList },
      { path: 'products/new', component: ProductForm },
      { path: 'products/:id/edit', component: ProductForm },

      // Bills
      { path: 'bills', component: BillList },
      { path: 'bills/new', component: BillForm },
      { path: 'bills/:id', component: BillDetails },
    ]
  },

  /* ================= FALLBACK ================= */
  { path: '**', redirectTo: 'login' }
];
