import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CustomerService} from '../../../core/services/customer.service';
import {Router, RouterLink} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {debounceTime, distinctUntilChanged} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-customer-form',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    NgIf
  ],
  templateUrl: './customer-form.html',
  styleUrl: './customer-form.css',
})
export class CustomerForm implements OnInit {
  customerForm!: FormGroup;
  emailExists: boolean = false;
  checkingEmail: boolean = false;

  constructor(
    private fb: FormBuilder,
    private service: CustomerService,
    private router: Router
  ) {}

  ngOnInit() {
    this.customerForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.minLength(8)]],
      street: [''],
      city: [''],
      country: [''],
      postalCode: ['']
    });

    // Vérification email en temps réel
    this.customerForm.get('email')?.valueChanges.pipe(
      debounceTime(500),
      distinctUntilChanged(),
      switchMap(email => {
        if (email && this.customerForm.get('email')?.valid) {
          this.checkingEmail = true;
          return this.service.checkEmail(email);
        }
        return [];
      })
    ).subscribe({
      next: (result: any) => {
        this.emailExists = result.exists;
        this.checkingEmail = false;
      },
      error: () => {
        this.checkingEmail = false;
      }
    });
  }

  submit() {
    if (this.customerForm.invalid || this.emailExists) {
      return;
    }

    this.service.create(this.customerForm.value).subscribe({
      next: (customer) => {
        alert(`Client créé avec succès! ID: ${customer.id}`);
        this.router.navigateByUrl('/customers');
      },
      error: (err: HttpErrorResponse) => {
        console.error(err);

        if (err.status === 409) {
          alert('Email déjà existant: ' + (err.error.message || err.error));
        } else if (err.status === 400) {
          alert('Données invalides: ' + JSON.stringify(err.error.errors || err.error.message));
        } else if (err.status === 401) {
          alert('Non authentifié. Veuillez vous reconnecter.');
          this.router.navigateByUrl('/login');
        } else if (err.status === 403) {
          alert('Accès refusé. Vous n\'avez pas les permissions nécessaires.');
        } else {
          alert('Une erreur inattendue s\'est produite');
        }
      }
    });
  }
}
