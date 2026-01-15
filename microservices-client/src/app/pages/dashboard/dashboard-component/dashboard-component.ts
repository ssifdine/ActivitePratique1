import { Component, OnInit } from '@angular/core';
import { CurrencyPipe, DatePipe, NgForOf, NgIf } from '@angular/common';
import { Navbar } from '../../../layout/navbar/navbar';
import {CustomerService} from '../../../core/services/customer.service';
import {ProductService} from '../../../core/services/product.service';
import {BillService} from '../../../core/services/bill.service';


@Component({
  selector: 'app-dashboard-component',
  standalone: true,
  imports: [
    NgIf,
    NgForOf,
    CurrencyPipe,
    DatePipe,
    Navbar
  ],
  templateUrl: './dashboard-component.html',
  styleUrl: './dashboard-component.css',
})
export class DashboardComponent implements OnInit {

  currentDate = new Date();

  // 🔹 Stats
  totalRevenue = 0;
  totalCustomers = 0;
  totalProducts = 0;
  totalInvoices = 0;

  // 🔹 Data
  recentInvoices: any[] = [];
  lowStockProducts: any[] = [];
  topProducts: any[] = [];

  constructor(
    private customerService: CustomerService,
    private productService: ProductService,
    private billService: BillService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.loadCustomers();
    this.loadProducts();
    this.loadBills();
  }

  // ================= CUSTOMERS =================
  loadCustomers(): void {
    this.customerService.getAll().subscribe({
      next: (customers) => {
        this.totalCustomers = customers.length;
      },
      error: (err) => console.error('Error loading customers', err)
    });
  }

  // ================= PRODUCTS =================
  loadProducts(): void {
    this.productService.getAll().subscribe({
      next: (products) => {
        this.totalProducts = products.length;

        // Low stock (quantity < 5)
        this.lowStockProducts = products.filter(p => p.quantity < 5);

        // Top products (ex: les 3 plus chers)
        this.topProducts = [...products]
          .sort((a, b) => b.price - a.price)
          .slice(0, 3);
      },
      error: (err) => console.error('Error loading products', err)
    });
  }

  // ================= BILLS =================
  loadBills(): void {
    this.billService.getAll().subscribe({
      next: (response) => {
        const bills = response.data;

        this.totalInvoices = bills.length;

        // Total revenue
        this.totalRevenue = bills.reduce(
          (sum, bill) => sum + bill.totalAmount,
          0
        );

        // Recent invoices (5 dernières)
        this.recentInvoices = [...bills]
          .sort((a, b) => b.id - a.id)
          .slice(0, 5);
      },
      error: (err) => console.error('Error loading bills', err)
    });
  }
}
