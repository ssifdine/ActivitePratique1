import {Component, OnInit} from '@angular/core';
import {CreateBill, Product} from '../../../core/models/bill.model';
import {BillService} from '../../../core/services/bill.service';
import {CustomerService} from '../../../core/services/customer.service';
import {ProductService} from '../../../core/services/product.service';
import {Router, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {CurrencyPipe, NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-bill-form',
  imports: [
    FormsModule,
    NgForOf,
    CurrencyPipe,
    RouterLink,
    NgIf
  ],
  templateUrl: './bill-form.html',
  styleUrl: './bill-form.css',
})
export class BillForm implements OnInit {

  customers: any[] = [];
  products: Product[] = [];

  bill: CreateBill = {
    customerId: 0,
    productItems: []
  };

  constructor(
    private billService: BillService,
    private customerService: CustomerService,
    private productService: ProductService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.loadCustomers();
    this.loadProducts();
    this.addItem(); // ligne par défaut
  }

  loadCustomers() {
    this.customerService.getAll().subscribe(res => {
      this.customers = res;
    });
  }

  loadProducts() {
    this.productService.getAll().subscribe(data => {
      this.products = data;
    });
  }

  addItem() {
    this.bill.productItems.push({
      productId: 0,
      quantity: 1
    });
  }

  removeItem(index: number) {
    this.bill.productItems.splice(index, 1);
  }

  // Nouvelle méthode pour gérer le changement de produit
  onProductChange(index: number) {
    // Convertir en nombre car ngModel retourne une chaîne depuis le select
    this.bill.productItems[index].productId = Number(this.bill.productItems[index].productId);
  }

  // Nouvelle méthode pour gérer le changement de quantité
  onQuantityChange(index: number) {
    // S'assurer que la quantité est au minimum 1
    if (this.bill.productItems[index].quantity < 1) {
      this.bill.productItems[index].quantity = 1;
    }
    // Convertir en nombre si nécessaire
    this.bill.productItems[index].quantity = Number(this.bill.productItems[index].quantity);
  }

  submit() {
    // Validation avant soumission
    if (this.bill.customerId === 0) {
      alert('Veuillez sélectionner un client');
      return;
    }

    if (this.bill.productItems.length === 0) {
      alert('Veuillez ajouter au moins un produit');
      return;
    }

    // Vérifier que tous les produits sont sélectionnés
    const hasInvalidProducts = this.bill.productItems.some(item => item.productId === 0);
    if (hasInvalidProducts) {
      alert('Veuillez sélectionner un produit pour chaque ligne');
      return;
    }

    this.billService.create(this.bill).subscribe(() => {
      this.router.navigate(['/bills']);
    });
  }

  getProductPrice(productId: number): number {
    // Convertir en nombre si c'est une chaîne
    const id = Number(productId);
    const product = this.products.find(p => p.id === id);
    return product ? product.price : 0;
  }

  getLineTotal(item: any): number {
    // Convertir les valeurs en nombres pour être sûr
    const productId = Number(item.productId);
    const quantity = Number(item.quantity);
    return this.getProductPrice(productId) * quantity;
  }

  getTotal(): number {
    return this.bill.productItems.reduce((total, item) => {
      return total + this.getProductPrice(item.productId) * item.quantity;
    }, 0);
  }

  getTax(): number {
    return this.getTotal() * 0.20; // 20% de taxe
  }

  getTotalWithTax(): number {
    return this.getTotal() + this.getTax();
  }
}
