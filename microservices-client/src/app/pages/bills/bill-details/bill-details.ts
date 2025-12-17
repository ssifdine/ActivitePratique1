import {Component, OnInit} from '@angular/core';
import {BillDetail} from '../../../core/models/bill.model';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {BillService} from '../../../core/services/bill.service';
import {CurrencyPipe, DatePipe, NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-bill-details',
  imports: [
    CurrencyPipe,
    DatePipe,
    NgForOf,
    NgIf,
    RouterLink
  ],
  templateUrl: './bill-details.html',
  styleUrl: './bill-details.css',
})
export class BillDetails implements OnInit {

  bill!: BillDetail;

  constructor(
    private route: ActivatedRoute,
    private billService: BillService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.billService.getById(id).subscribe(res => {
      this.bill = res.data;
    });
  }

}
