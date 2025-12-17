import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ApiResponse, BillDetail, BillSummary, CreateBill} from '../models/bill.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BillService {

  private api = 'http://localhost:8888/BILLING-SERVICE/api/bills';

  constructor(private http: HttpClient) {}

  getAll(): Observable<ApiResponse<BillSummary[]>> {
    return this.http.get<ApiResponse<BillSummary[]>>(this.api);
  }

  getById(id: number): Observable<ApiResponse<BillDetail>> {
    return this.http.get<ApiResponse<BillDetail>>(`${this.api}/${id}`);
  }

  create(bill: CreateBill): Observable<ApiResponse<BillDetail>> {
    return this.http.post<ApiResponse<BillDetail>>(this.api, bill);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.api}/${id}`);
  }

}
