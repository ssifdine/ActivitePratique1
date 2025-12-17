import {Component, OnInit} from '@angular/core';
import {CurrencyPipe, DatePipe, NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-dashboard-component',
  imports: [
    NgIf,
    CurrencyPipe,
    DatePipe,
    NgForOf
  ],
  templateUrl: './dashboard-component.html',
  styleUrl: './dashboard-component.css',
})
export class DashboardComponent implements OnInit {
  currentDate = new Date();

  // Stats
  totalRevenue: number = 0;
  totalCustomers: number = 0;
  totalProducts: number = 0;
  totalInvoices: number = 0;

  // Recent data
  recentInvoices: any[] = [];
  lowStockProducts: any[] = [];
  topProducts: any[] = [];

  constructor(
    // Injectez vos services ici
    // private customerService: CustomerService,
    // private productService: ProductService,
    // private billService: BillService
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    // Charger les statistiques
    // this.customerService.getAll().subscribe(customers => {
    //   this.totalCustomers = customers.length;
    // });

    // this.productService.getAll().subscribe(products => {
    //   this.totalProducts = products.length;
    //   this.lowStockProducts = products.filter(p => p.quantity <= 10);
    //   this.topProducts = products.slice(0, 3); // Top 3 produits
    // });

    // this.billService.getAll().subscribe(bills => {
    //   this.totalInvoices = bills.length;
    //   this.totalRevenue = bills.reduce((sum, bill) => sum + bill.totalAmount, 0);
    //   this.recentInvoices = bills.slice(0, 5); // 5 dernières factures
    // });
  }

}
