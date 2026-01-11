import { Routes } from '@angular/router';
import {CustomerList} from './pages/customers/customer-list/customer-list';
import {CustomerForm} from './pages/customers/customer-form/customer-form';
import {ProductList} from './pages/products/product-list/product-list';
import {ProductForm} from './pages/products/product-form/product-form';
import {BillList} from './pages/bills/bill-list/bill-list';
import {BillForm} from './pages/bills/bill-form/bill-form';
import {CustomerEdit} from './pages/customers/customer-edit/customer-edit';
import {BillDetails} from './pages/bills/bill-details/bill-details';
import {DashboardComponent} from './pages/dashboard/dashboard-component/dashboard-component';
import {Login} from './pages/auth/login/login';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login' , component: Login },

  { path: 'customers', component: CustomerList },
  { path: 'customers/new', component: CustomerForm },
  { path: 'customers/:id/edit', component: CustomerEdit },

  { path: 'products', component: ProductList },
  { path: 'products/new', component: ProductForm },
  { path: 'products/:id/edit', component: ProductForm },

  { path: 'bills', component: BillList },
  { path: 'bills/new', component: BillForm },
  { path: 'bills/:id', component: BillDetails },

  { path: 'dashboard', component: DashboardComponent },

];
