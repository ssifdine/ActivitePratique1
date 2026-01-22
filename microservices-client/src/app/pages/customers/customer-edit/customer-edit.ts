import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CustomerService} from '../../../core/services/customer.service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {Customer} from '../../../core/models/customer.model';
import {HttpErrorResponse} from '@angular/common/http';
import {debounceTime, distinctUntilChanged} from 'rxjs';
import {switchMap} from 'rxjs/operators';

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
  customer?: Customer;
  emailTaken: boolean = false;
  checkingEmail: boolean = false;

  constructor(
    private fb: FormBuilder,
    private service: CustomerService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.customerId = Number(this.route.snapshot.paramMap.get('id'));

    this.customerForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.minLength(8)]],
      street: [''],
      city: [''],
      country: [''],
      postalCode: ['']
    });

    // Charger le client
    this.service.getById(this.customerId).subscribe({
      next: (data: Customer) => {
        this.customer = data;
        this.customerForm.patchValue(data);
        if (data.email != null) {
          this.setupEmailValidation(data.email);
        }
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 404) {
          alert('Client introuvable');
          this.router.navigateByUrl('/customers');
        } else {
          console.error(err);
          alert('Erreur lors du chargement du client');
        }
      }
    });
  }

  setupEmailValidation(originalEmail: string) {
    this.customerForm.get('email')?.valueChanges.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(email => {
        if (email && email !== originalEmail && this.customerForm.get('email')?.valid) {
          this.checkingEmail = true;
          return this.service.validateUniqueEmail(email, this.customerId);
        }
        this.emailTaken = false;
        return [];
      })
    ).subscribe({
      next: (result: any) => {
        this.emailTaken = !result.isUnique;
        this.checkingEmail = false;
      },
      error: () => {
        this.checkingEmail = false;
      }
    });
  }

  submit() {
    if (this.customerForm.invalid || this.emailTaken) {
      return;
    }

    this.service.update(this.customerId, this.customerForm.value).subscribe({
      next: (data: Customer) => {
        alert('Client mis à jour avec succès!');
        this.router.navigateByUrl('/customers');
      },
      error: (err: HttpErrorResponse) => {
        console.error(err);
        if (err.status === 409) {
          alert('Email déjà utilisé par un autre client');
        } else if (err.status === 404) {
          alert('Client introuvable');
        } else if (err.status === 400) {
          alert('Données invalides: ' + JSON.stringify(err.error.errors || err.error.message));
        } else {
          alert('Erreur lors de la mise à jour');
        }
      }
    });
  }
}
