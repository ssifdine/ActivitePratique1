import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CustomerService} from '../../../core/services/customer.service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {Customer} from '../../../core/models/customer.model';

@Component({
  selector: 'app-customer-edit',
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './customer-edit.html',
  styleUrl: './customer-edit.css',
})
export class CustomerEdit implements OnInit {


  customerForm!: FormGroup;
  customerId!: number;

  constructor(
    private fb: FormBuilder,
    private service: CustomerService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Récupérer l'ID depuis l'URL
    this.customerId = Number(this.route.snapshot.paramMap.get('id'));

    // Initialiser le formulaire
    this.customerForm = this.fb.group({
      fullName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      street: ['', Validators.required],
      city: ['', Validators.required],
      country: ['', Validators.required],
      postalCode: ['', Validators.required]
    });

    // Charger le client
    this.service.getById(this.customerId).subscribe({
      next: (data: Customer) => this.customerForm.patchValue(data),
      error: err => console.error(err)
    });
  }

  submit() {
    if (this.customerForm.invalid) return;

    this.service.update(this.customerId, this.customerForm.value).subscribe({
      next: (data: Customer) => {
        alert('Customer updated successfully!');
        this.router.navigateByUrl('/customers');
      },
      error: err => {
        console.error(err);
        alert('Error updating customer');
      }
    });
  }
}
