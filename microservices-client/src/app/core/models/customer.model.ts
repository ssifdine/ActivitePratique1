export interface Customer {
  id?: number;
  fullName: string;
  email?: string;
  phone: string;
  street?: string;
  city?: string;
  country?: string;
  postalCode?: string;
  active?: boolean;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: string;
  lastModifiedBy?: string;
}

export interface CustomerStats {
  totalCustomers: number;
  activeCustomers: number;
  inactiveCustomers: number;
  newCustomersToday: number;
  newCustomersThisWeek: number;
  newCustomersThisMonth: number;
  activePercentage: number;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number; // current page
  size: number;
  first?: boolean;
  last?: boolean;
}

