import {Component, OnInit} from '@angular/core';
import {CustomerService} from '../../../core/services/customer.service';
import {RouterLink} from '@angular/router';
import {NgForOf, NgIf} from '@angular/common';
import {Customer} from '../../../core/models/customer.model';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-customer-list',
  imports: [
    RouterLink,
    NgForOf,
    FormsModule,
    NgIf
  ],
  templateUrl: './customer-list.html',
  styleUrl: './customer-list.css',
})
export class CustomerList implements OnInit {


  customers: Customer[] = [];
  keyword: string = '';
  page: number = 0;
  size: number = 10;
  totalPages: number = 0;

  constructor(private service: CustomerService) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers() {
    this.service.search(this.keyword, this.page, this.size).subscribe(res => {
      this.customers = res.content;
      this.totalPages = res.totalPages;
    });
  }

  search() {
    this.page = 0; // reset page on new search
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
