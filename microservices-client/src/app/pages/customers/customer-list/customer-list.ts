import { Component, OnInit } from '@angular/core';
import { CustomerService } from '../../../core/services/customer.service';
import {Customer, CustomerStats} from '../../../core/models/customer.model';
import { AuthService } from '../../../core/services/auth-service';
import {Navbar} from '../../../layout/navbar/navbar';
import {RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {DatePipe, DecimalPipe, NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-customer-list',
  templateUrl: './customer-list.html',
  imports: [
    Navbar,
    RouterLink,
    FormsModule,
    NgIf,
    NgForOf,
    DatePipe,
    DecimalPipe
  ],
  styleUrls: ['./customer-list.css']
})
export class CustomerList implements OnInit {
  customers: Customer[] = [];
  keyword: string = '';
  page: number = 0;
  size: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;

  // Nouveaux filtres avancés
  showAdvancedFilters: boolean = false;
  cityFilter: string = '';
  countryFilter: string = '';
  activeFilter?: boolean;

  // Statistiques
  stats?: CustomerStats;
  showStats: boolean = false;

  // Customers supprimés
  showDeleted: boolean = false;

  // Sélection multiple
  selectedCustomers: Set<number> = new Set();
  selectAll: boolean = false;

  isAdmin = false;

  constructor(
    private service: CustomerService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.loadCustomers();
    this.loadStats();
  }

  loadCustomers() {
    if (this.showDeleted) {
      this.service.getDeleted().subscribe(res => {
        this.customers = res;
        this.totalPages = 1;
        this.totalElements = res.length;
      });
    } else if (this.showAdvancedFilters) {
      this.service.advancedSearch({
        keyword: this.keyword,
        city: this.cityFilter,
        country: this.countryFilter,
        active: this.activeFilter,
        page: this.page,
        size: this.size
      }).subscribe(res => {
        this.customers = res.content;
        this.totalPages = res.totalPages;
        this.totalElements = res.totalElements;
      });
    } else {
      this.service.search(this.keyword, this.page, this.size).subscribe(res => {
        this.customers = res.content;
        this.totalPages = res.totalPages;
        this.totalElements = res.totalElements;
      });
    }
  }

  loadStats() {
    this.service.getStats().subscribe(stats => {
      this.stats = stats;
    });
  }

  search() {
    this.page = 0;
    this.loadCustomers();
  }

  clearFilters() {
    this.keyword = '';
    this.cityFilter = '';
    this.countryFilter = '';
    this.activeFilter = undefined;
    this.page = 0;
    this.loadCustomers();
  }

  toggleAdvancedFilters() {
    this.showAdvancedFilters = !this.showAdvancedFilters;
    if (!this.showAdvancedFilters) {
      this.clearFilters();
    }
  }

  toggleStats() {
    this.showStats = !this.showStats;
  }

  toggleDeleted() {
    this.showDeleted = !this.showDeleted;
    this.page = 0;
    this.loadCustomers();
  }

  deleteCustomer(id: number) {
    if (confirm('Supprimer ce client (soft delete) ?')) {
      this.service.delete(id).subscribe(() => {
        this.loadCustomers();
        this.loadStats();
      });
    }
  }

  hardDeleteCustomer(id: number) {
    if (confirm('⚠️ ATTENTION: Supprimer définitivement ce client ? Cette action est IRRÉVERSIBLE !')) {
      this.service.hardDelete(id).subscribe(() => {
        this.loadCustomers();
        this.loadStats();
      });
    }
  }

  restoreCustomer(id: number) {
    this.service.restore(id).subscribe(() => {
      this.loadCustomers();
      this.loadStats();
    });
  }

  // Sélection multiple
  toggleSelection(id: number) {
    if (this.selectedCustomers.has(id)) {
      this.selectedCustomers.delete(id);
    } else {
      this.selectedCustomers.add(id);
    }
    this.updateSelectAll();
  }

  toggleSelectAll() {
    if (this.selectAll) {
      this.customers.forEach(c => {
        if (c.id) this.selectedCustomers.add(c.id);
      });
    } else {
      this.selectedCustomers.clear();
    }
  }

  updateSelectAll() {
    this.selectAll = this.customers.length > 0 &&
      this.customers.every(c => c.id && this.selectedCustomers.has(c.id));
  }

  deleteSelectedCustomers() {
    const ids = Array.from(this.selectedCustomers);
    if (ids.length === 0) {
      alert('Aucun client sélectionné');
      return;
    }

    if (confirm(`Supprimer ${ids.length} client(s) ?`)) {
      this.service.softDeleteMultiple(ids).subscribe(res => {
        alert(`${res.deleted} client(s) supprimé(s)`);
        this.selectedCustomers.clear();
        this.loadCustomers();
        this.loadStats();
      });
    }
  }

  restoreSelectedCustomers() {
    const ids = Array.from(this.selectedCustomers);
    if (ids.length === 0) {
      alert('Aucun client sélectionné');
      return;
    }

    this.service.restoreMultiple(ids).subscribe(res => {
      alert(`${res.restored} client(s) restauré(s)`);
      this.selectedCustomers.clear();
      this.loadCustomers();
      this.loadStats();
    });
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

  // Export
  exportToCsv() {
    this.service.exportCsv().subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `customers-${new Date().toISOString()}.csv`;
      a.click();
    }, error => {
      if (error.status === 501) {
        alert('Export CSV pas encore implémenté');
      }
    });
  }

  exportToExcel() {
    this.service.exportExcel().subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `customers-${new Date().toISOString()}.xlsx`;
      a.click();
    }, error => {
      if (error.status === 501) {
        alert('Export Excel pas encore implémenté');
      }
    });
  }
}
