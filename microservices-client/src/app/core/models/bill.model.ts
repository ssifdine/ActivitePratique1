export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: Date;
}

export interface BillSummary {
  id: number;
  billingDate: Date;
  customerId: number;
  customerName: string;
  itemCount: number;
  totalAmount: number;
}

export interface BillDetail {
  id: number;
  billingDate: Date;
  customerId: number;
  customer: Customer;
  productItems: ProductItem[];
  subtotal: number;
  tax: number;
  totalAmount: number;
}

export interface Customer {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  street: string;
  city: string;
  country: string;
  postalCode: string;
}

export interface ProductItem {
  id: number;
  productId: number;
  product: Product;
  quantity: number;
  price: number;
  totalPrice: number;
}

export interface Product {
  id?: number;
  name: string;
  description: string;
  price: number;
  quantity: number;
}

export interface CreateBill {
  customerId: number;
  productItems: CreateProductItem[];
}

export interface CreateProductItem {
  productId: number;
  quantity: number;
}
