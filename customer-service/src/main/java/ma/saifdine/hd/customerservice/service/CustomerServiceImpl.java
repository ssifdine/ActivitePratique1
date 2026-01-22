package ma.saifdine.hd.customerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.saifdine.hd.customerservice.dtos.CustomerRequestDTO;
import ma.saifdine.hd.customerservice.dtos.CustomerResponseDTO;
import ma.saifdine.hd.customerservice.dtos.CustomerStatsDTO;
import ma.saifdine.hd.customerservice.entity.Customer;
import ma.saifdine.hd.customerservice.exception.CustomerNotFoundException;
import ma.saifdine.hd.customerservice.mapper.CustomerMapper;
import ma.saifdine.hd.customerservice.repository.CustomerRepository;
import ma.saifdine.hd.customerservice.repository.CustomerSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    /**
     * Créer un customer
     */

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {
        log.info("Creating customer with email: {}", dto.getEmail());

        // Vérifier si l'email existe déjà
        Optional<Customer> existing = customerRepository.findByEmail(dto.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        Customer customer = mapper.toEntity(dto);
        Customer saved = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", saved.getId());
        return mapper.toDTO(saved);
    }


    /**
     * Récupérer un customer
     */
    @Override
    public CustomerResponseDTO getCustomer(Long id) {
        log.debug("Fetching customer with ID: {} from database", id);

        Customer customer = customerRepository.findById(id)
                .filter(Customer::isActive) // Filtre soft delete
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return mapper.toDTO(customer);
    }

    /**
     * Liste
     */
    @Override
    public List<CustomerResponseDTO> getAllCustomers() {

        log.debug("Fetching all active customers from database");

        return customerRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /**
     * Mise à jour
     */
    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto) {
        log.info("Updating customer with ID: {}", id);

        Customer existing = customerRepository.findById(id)
                .filter(Customer::isActive)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        // Vérifier email unique si changé
        if (!existing.getEmail().equals(dto.getEmail())) {
            Optional<Customer> emailExists = customerRepository.findByEmail(dto.getEmail());
            if (emailExists.isPresent() && !emailExists.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
            }
        }

        existing.setFullName(dto.getFullName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.getAddress().setStreet(dto.getStreet());
        existing.getAddress().setCity(dto.getCity());
        existing.getAddress().setCountry(dto.getCountry());
        existing.getAddress().setPostalCode(dto.getPostalCode());

        Customer updated = customerRepository.save(existing);

        log.info("Customer updated successfully with ID: {}", id);

        return mapper.toDTO(updated);
    }

    /**
     * Soft delete
     */
    @Override
    public void deleteCustomer(Long id) {
        log.info("Soft deleting customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customer.softDelete();
        customerRepository.save(customer);

        log.info("Customer soft deleted successfully with ID: {}", id);
    }

    /**
     * Hard delete (admin only)
     */
    @Override
    public void hardDeleteCustomer(Long id) {
        log.warn("Hard deleting customer with ID: {}", id);

        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }

        customerRepository.deleteById(id);
        log.warn("Customer permanently deleted with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getInactiveCustomers() {
        log.debug("Fetching inactive (soft deleted) customers");

        return customerRepository.findByActiveFalse()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public int restoreMultipleCustomers(List<Long> ids) {
        log.info("Restoring multiple customers: {}", ids);

        int updatedCount = customerRepository.restoreMultiple(ids);

        log.info("Number of customers restored: {}", updatedCount);
        return updatedCount;
    }

    @Override
    @Transactional
    public int softDeleteMultipleCustomers(List<Long> ids) {
        log.info("Soft deleting multiple customers: {}", ids);

        int updatedCount = customerRepository.softDeleteMultiple(ids);

        log.info("Number of customers soft deleted: {}", updatedCount);
        return updatedCount;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerStatsDTO getStatistics() {
        log.debug("Fetching customer statistics");

        long total = customerRepository.count();
        long active = customerRepository.countActiveCustomers();
        long inactive = customerRepository.countInactiveCustomers();

        // Nouveaux clients
        long newToday = customerRepository.findTodayCustomers().size();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(java.time.DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long newThisWeek = customerRepository.findCustomersCreatedAfter(startOfWeek).size();

        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long newThisMonth = customerRepository.findCustomersCreatedAfter(startOfMonth).size();

        double activePercent = total > 0 ? (active * 100.0 / total) : 0;

        return CustomerStatsDTO.builder()
                .totalCustomers(total)
                .activeCustomers(active)
                .inactiveCustomers(inactive)
                .newCustomersToday(newToday)
                .newCustomersThisWeek(newThisWeek)
                .newCustomersThisMonth(newThisMonth)
                .activePercentage(activePercent)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getCustomersCountByCountry() {
        log.debug("Fetching customer count by country");

        List<Object[]> results = customerRepository.countCustomersByCountry();

        // Convertir List<Object[]> en Map<String, Long>
        Map<String, Long> map = results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getCustomersCountByCity() {
        log.debug("Fetching customer count by city");

        List<Object[]> results = customerRepository.countCustomersByCity();

        // Convertir List<Object[]> en Map<String, Long>
        Map<String, Long> map = results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));

        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerStatsDTO getStatisticsBetween(LocalDateTime start, LocalDateTime end) {
        log.debug("Fetching customer statistics between {} and {}", start, end);

        Object result = customerRepository.getStatsBetween(start, end);
        Object[] data = (Object[]) result;

        long total = ((Number) data[0]).longValue();
        long active = ((Number) data[1]).longValue();
        long inactive = ((Number) data[2]).longValue();
        double activePercent = total > 0 ? (active * 100.0 / total) : 0;

        return CustomerStatsDTO.builder()
                .totalCustomers(total)
                .activeCustomers(active)
                .inactiveCustomers(inactive)
                .activePercentage(activePercent)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getTodayCustomers() {
        log.debug("Fetching today's customers");

        List<Customer> customers = customerRepository.findTodayCustomers();

        return customers.stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        log.debug("Checking if email exists: {}", email);
        return customerRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailUniqueForCustomer(String email, Long customerId) {
        log.debug("Checking if email {} is unique for customer ID {}", email, customerId);
        // true = unique, false = déjà utilisé
        return !customerRepository.existsByEmailAndIdNot(email, customerId);
    }



    /**
     * Restaurer un client soft deleted
     */
    @Override
    public CustomerResponseDTO restoreCustomer(Long id) {
        log.info("Restoring customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customer.restore();
        Customer restored = customerRepository.save(customer);

        log.info("Customer restored successfully with ID: {}", id);
        return mapper.toDTO(restored);
    }



    /**
     * Recherche paginée (pas de cache pour requêtes dynamiques)
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> searchCustomerByPagenation(int page, int size, String keyword) {
        log.debug("Searching customers with keyword: {}, page: {}, size: {}", keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository.searchCustomerByPagenation("%" + keyword + "%",pageable);
        return customerPage.map(mapper::toDTO);
    }

    @Override
    public Page<CustomerResponseDTO> advancedSearch(String keyword, String city, String country, LocalDateTime createdAfter, LocalDateTime createdBefore, Boolean active, int page, int size) {
        log.debug("Advanced search customers");

        Pageable pageable = PageRequest.of(page, size);

        Specification<Customer> spec = CustomerSpecifications.filterBy(
                keyword,
                city,
                country,
                createdAfter,
                createdBefore,
                active
        );

        Page<Customer> customers = customerRepository.findAll(spec, pageable);

        return customers.map(mapper::toDTO);
    }

    @Override
    public List<CustomerResponseDTO> getCustomersByCity(String city) {
        log.debug("Fetching customers by city: {}", city);

        return customerRepository.findByCity(city)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<CustomerResponseDTO> getCustomersByCountry(String country) {
        log.debug("Fetching customers by country: {}", country);

        return customerRepository.findByCountry(country)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }




}