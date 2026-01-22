import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Customer, CustomerStats, PaginatedResponse} from '../models/customer.model';
import {Observable} from 'rxjs';


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

  advancedSearch(filters: {
    keyword?: string;
    city?: string;
    country?: string;
    createdAfter?: string;
    createdBefore?: string;
    active?: boolean;
    page?: number;
    size?: number;
  }): Observable<PaginatedResponse<Customer>> {
    let params = new HttpParams();

    if (filters.keyword) params = params.set('keyword', filters.keyword);
    if (filters.city) params = params.set('city', filters.city);
    if (filters.country) params = params.set('country', filters.country);
    if (filters.createdAfter) params = params.set('createdAfter', filters.createdAfter);
    if (filters.createdBefore) params = params.set('createdBefore', filters.createdBefore);
    if (filters.active !== undefined) params = params.set('active', filters.active.toString());
    params = params.set('page', (filters.page || 0).toString());
    params = params.set('size', (filters.size || 10).toString());

    return this.http.get<PaginatedResponse<Customer>>(`${this.api}/advanced-search`, { params });
  }

  getByCity(city: string): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.api}/by-city/${city}`);
  }

  getByCountry(country: string): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.api}/by-country/${country}`);
  }

  restore(id: number): Observable<Customer> {
    return this.http.patch<Customer>(`${this.api}/${id}/restore`, {});
  }

  hardDelete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}/hard`);
  }

  getDeleted(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.api}/deleted`);
  }

  restoreMultiple(ids: number[]): Observable<{ restored: number }> {
    return this.http.post<{ restored: number }>(`${this.api}/restore-multiple`, ids);
  }

  softDeleteMultiple(ids: number[]): Observable<{ deleted: number }> {
    return this.http.delete<{ deleted: number }>(`${this.api}/soft-delete-multiple`, { body: ids });
  }

  getStats(): Observable<CustomerStats> {
    return this.http.get<CustomerStats>(`${this.api}/stats`);
  }

  getStatsByCountry(): Observable<{ [key: string]: number }> {
    return this.http.get<{ [key: string]: number }>(`${this.api}/stats/by-country`);
  }

  getStatsByCity(): Observable<{ [key: string]: number }> {
    return this.http.get<{ [key: string]: number }>(`${this.api}/stats/by-city`);
  }

  getStatsByPeriod(start: string, end: string): Observable<CustomerStats> {
    let params = new HttpParams()
      .set('start', start)
      .set('end', end);
    return this.http.get<CustomerStats>(`${this.api}/stats/period`, { params });
  }

  getTodayCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.api}/today`);
  }

  checkEmail(email: string): Observable<{ exists: boolean }> {
    return this.http.get<{ exists: boolean }>(`${this.api}/check-email`, {
      params: { email }
    });
  }

  validateUniqueEmail(email: string, customerId: number): Observable<{ isUnique: boolean }> {
    return this.http.get<{ isUnique: boolean }>(`${this.api}/validate-unique-email`, {
      params: { email, customerId: customerId.toString() }
    });
  }

  exportCsv(): Observable<Blob> {
    return this.http.get(`${this.api}/export/csv`, {
      responseType: 'blob'
    });
  }

  exportExcel(): Observable<Blob> {
    return this.http.get(`${this.api}/export/excel`, {
      responseType: 'blob'
    });
  }


}
