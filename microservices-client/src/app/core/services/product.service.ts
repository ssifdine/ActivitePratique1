import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Product} from '../models/product.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProductService {

  private baseUrl = 'http://localhost:8888/INVENTORY-SERVICE/api/products';

  constructor(private http: HttpClient) { }

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(this.baseUrl);
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/${id}`);
  }

  create(product: Product): Observable<Product> {
    return this.http.post<Product>(this.baseUrl, product);
  }

  update(id: number, product: Product): Observable<Product> {
    return this.http.put<Product>(`${this.baseUrl}/${id}`, product);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
