import {Component, OnInit} from '@angular/core';
import {Product} from '../../../core/models/product.model';
import {ProductService} from '../../../core/services/product.service';
import {RouterLink} from '@angular/router';
import {DecimalPipe, NgClass, NgForOf, NgIf} from '@angular/common';
import {Navbar} from '../../../layout/navbar/navbar';
import {AuthService} from '../../../core/services/auth-service';

@Component({
  selector: 'app-product-list',
  imports: [
    RouterLink,
    NgForOf,
    NgClass,
    DecimalPipe,
    NgIf,
    Navbar
  ],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css',
})
export class ProductList implements OnInit {

  products: Product[] = [];

  isAdmin = false;


  constructor(private productService: ProductService,
              private authService: AuthService) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin(); // récupère le rôle
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
