import { Component, OnInit } from '@angular/core';
import { CustomerService } from '../../../core/services/customer.service';
import { Customer } from '../../../core/models/customer.model';
import { AuthService } from '../../../core/services/auth-service';
import {Navbar} from '../../../layout/navbar/navbar';
import {RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-customer-list',
  templateUrl: './customer-list.html',
  imports: [
    Navbar,
    RouterLink,
    FormsModule,
    NgIf,
    NgForOf
  ],
  styleUrls: ['./customer-list.css']
})
export class CustomerList implements OnInit {

  customers: Customer[] = [];
  keyword: string = '';
  page: number = 0;
  size: number = 10;
  totalPages: number = 0;

  isAdmin = false;

  constructor(
    private service: CustomerService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin(); // récupère le rôle
    this.loadCustomers();
  }

  loadCustomers() {
    this.service.search(this.keyword, this.page, this.size).subscribe(res => {
      this.customers = res.content;
      this.totalPages = res.totalPages;
    });
  }

  search() {
    this.page = 0;
    this.loadCustomers();
  }

  deleteCustomer(id: number) {
    if (confirm('Delete this customer?')) {
      this.service.delete(id).subscribe(() => {
        this.loadCustomers();
      });
    }
  }

  previousPage() {
    if (this.page > 0) {
      this.page--;
      this.loadCustomers();
    }
  }

  nextPage() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadCustomers();
    }
  }
}
