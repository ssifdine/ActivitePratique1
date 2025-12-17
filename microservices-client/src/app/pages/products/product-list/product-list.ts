import {Component, OnInit} from '@angular/core';
import {Product} from '../../../core/models/product.model';
import {ProductService} from '../../../core/services/product.service';
import {RouterLink} from '@angular/router';
import {DecimalPipe, NgClass, NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-product-list',
  imports: [
    RouterLink,
    NgForOf,
    NgClass,
    DecimalPipe,
    NgIf
  ],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css',
})
export class ProductList implements OnInit {

  products: Product[] = [];

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts() {
    this.productService.getAll().subscribe(data => this.products = data);
  }

  deleteProduct(id: number) {
    if(confirm('Are you sure you want to delete this product?')) {
      this.productService.delete(id).subscribe(() => this.loadProducts());
    }
  }
}
