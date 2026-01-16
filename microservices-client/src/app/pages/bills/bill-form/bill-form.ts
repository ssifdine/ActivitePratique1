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
  errorMessage: string = '';
  isSubmitting: boolean = false;

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
    this.addItem();
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
    this.errorMessage = ''; // Clear error when items change
  }

  onProductChange(index: number) {
    this.bill.productItems[index].productId = Number(this.bill.productItems[index].productId);
    this.errorMessage = ''; // Clear error when product changes
  }

  onQuantityChange(index: number) {
    if (this.bill.productItems[index].quantity < 1) {
      this.bill.productItems[index].quantity = 1;
    }
    this.bill.productItems[index].quantity = Number(this.bill.productItems[index].quantity);
    this.errorMessage = ''; // Clear error when quantity changes
  }

  // Get product by ID
  getProduct(productId: number): Product | undefined {
    const id = Number(productId);
    return this.products.find(p => p.id === id);
  }

  // Check if quantity exceeds available stock
  isQuantityExceedingStock(item: any): boolean {
    const product = this.getProduct(item.productId);
    if (!product || item.productId === 0) return false;
    return item.quantity > product.quantity;
  }

  // Get available stock for a product
  getAvailableStock(productId: number): number {
    const product = this.getProduct(productId);
    return product ? product.quantity : 0;
  }

  // Validate stock before submission
  validateStock(): boolean {
    for (const item of this.bill.productItems) {
      const product = this.getProduct(item.productId);
      if (product && item.quantity > product.quantity) {
        this.errorMessage = `Insufficient stock for ${product.name}. Available: ${product.quantity}, Requested: ${item.quantity}`;
        return false;
      }
    }
    return true;
  }

  submit() {
    // Clear previous error
    this.errorMessage = '';

    // Validation
    if (this.bill.customerId === 0) {
      this.errorMessage = 'Please select a customer';
      return;
    }

    if (this.bill.productItems.length === 0) {
      this.errorMessage = 'Please add at least one product';
      return;
    }

    const hasInvalidProducts = this.bill.productItems.some(item => item.productId === 0);
    if (hasInvalidProducts) {
      this.errorMessage = 'Please select a product for each line';
      return;
    }

    // Validate stock availability
    if (!this.validateStock()) {
      return;
    }

    this.isSubmitting = true;

    this.billService.create(this.bill).subscribe({
      next: () => {
        this.router.navigate(['/bills']);
      },
      error: (error) => {
        this.isSubmitting = false;

        // Extract error message from response
        if (error.error && error.error.message) {
          this.errorMessage = error.error.message;
        } else if (error.message) {
          this.errorMessage = error.message;
        } else {
          this.errorMessage = 'An error occurred while creating the invoice. Please try again.';
        }

        // Scroll to top to show error
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    });
  }

  getProductPrice(productId: number): number {
    const product = this.getProduct(productId);
    return product ? product.price : 0;
  }

  getLineTotal(item: any): number {
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
    return this.getTotal() * 0.20;
  }

  getTotalWithTax(): number {
    return this.getTotal() + this.getTax();
  }
}
