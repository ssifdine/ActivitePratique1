import { Component } from '@angular/core';
import {Product} from '../../../core/models/product.model';
import {ProductService} from '../../../core/services/product.service';
import {ActivatedRoute, Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {NgClass, NgIf} from '@angular/common';

@Component({
  selector: 'app-product-form',
  imports: [
    FormsModule,
    NgClass,
    NgIf
  ],
  templateUrl: './product-form.html',
  styleUrl: './product-form.css',
})
export class ProductForm {
  product: Product = {
    name: '',
    description: '',
    price: 0,
    quantity: 1
  };

  isEditMode = false;
  productId!: number;

  constructor(
    private productService: ProductService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      this.isEditMode = true;
      this.productId = Number(idParam);
      this.loadProduct();
    }
  }

  loadProduct() {
    this.productService.getById(this.productId).subscribe({
      next: (data) => this.product = data,
      error: err => console.error(err)
    });
  }

  save() {
    if (this.isEditMode) {
      this.productService.update(this.productId, this.product).subscribe(() => {
        this.router.navigate(['/products']);
      });
    } else {
      this.productService.create(this.product).subscribe(() => {
        this.router.navigate(['/products']);
      });
    }
  }
}
