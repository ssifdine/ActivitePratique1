import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CustomerService} from '../../../core/services/customer.service';
import {Router, RouterLink} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-customer-form',
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './customer-form.html',
  styleUrl: './customer-form.css',
})
export class CustomerForm {
  customerForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private service: CustomerService,
    private router: Router
  ) {
    this.customerForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      street: ['', Validators.required],
      city: ['', Validators.required],
      country: ['', Validators.required],
      postalCode: ['', Validators.required]
    });
  }

  submit() {
    if (this.customerForm.invalid) return;

    this.service.create(this.customerForm.value).subscribe({
      next: () => {
        this.router.navigateByUrl('/customers');
      },
      error: (err: HttpErrorResponse) => {
        console.error(err);

        if (err.status === 409) {
          alert(err.error); // message backend
        } else {
          alert('Unexpected error occurred');
        }
      }

    });
  }
}
