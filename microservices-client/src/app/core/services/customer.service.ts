import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Customer} from '../models/customer.model';
import {Observable} from 'rxjs';

interface PaginatedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number; // current page
  size: number;
}

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private api = 'http://localhost:8888/CUSTOMER-SERVICE/api/customers';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<Customer[]>(this.api);
  }

  getById(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.api}/${id}`);
  }

  create(c: Customer) {
    return this.http.post<Customer>(this.api, c);
  }

  update(id: number, customer: Customer): Observable<Customer> {
    return this.http.put<Customer>(`${this.api}/${id}`, customer);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }

  search(keyword: string = '', page: number = 0, size: number = 10): Observable<PaginatedResponse<Customer>> {
    let params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PaginatedResponse<Customer>>(`${this.api}/search`, { params });
  }
}
