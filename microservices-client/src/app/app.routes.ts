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

import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // 🔓 Public
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  // 🔐 Protected
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },

  { path: 'customers', component: CustomerList, canActivate: [authGuard] },
  { path: 'customers/new', component: CustomerForm, canActivate: [authGuard] },
  { path: 'customers/:id/edit', component: CustomerEdit, canActivate: [authGuard] },

  { path: 'products', component: ProductList, canActivate: [authGuard] },
  { path: 'products/new', component: ProductForm, canActivate: [authGuard] },
  { path: 'products/:id/edit', component: ProductForm, canActivate: [authGuard] },

  { path: 'bills', component: BillList, canActivate: [authGuard] },
  { path: 'bills/new', component: BillForm, canActivate: [authGuard] },
  { path: 'bills/:id', component: BillDetails, canActivate: [authGuard] },
];
