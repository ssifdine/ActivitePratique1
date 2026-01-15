import {Component, OnInit} from '@angular/core';
import {BillSummary} from '../../../core/models/bill.model';
import {BillService} from '../../../core/services/bill.service';
import {RouterLink} from '@angular/router';
import {CurrencyPipe, NgForOf, NgIf} from '@angular/common';
import {Navbar} from '../../../layout/navbar/navbar';
import {AuthService} from '../../../core/services/auth-service';

@Component({
  selector: 'app-bill-list',
  imports: [
    RouterLink,
    CurrencyPipe,
    NgForOf,
    NgIf,
    Navbar
  ],
  templateUrl: './bill-list.html',
  styleUrl: './bill-list.css',
})
export class BillList implements OnInit {

  bills: BillSummary[] = [];
  isAdmin = false;


  constructor(private billService: BillService, private authService: AuthService) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin(); // récupère le rôle
    this.loadBills();
  }

  loadBills() {
    this.billService.getAll().subscribe(res => {
      this.bills = res.data;
    });
  }

  deleteBill(id: number) {
    if (confirm('Delete this bill?')) {
      this.billService.delete(id).subscribe(() => this.loadBills());
    }
  }
}
