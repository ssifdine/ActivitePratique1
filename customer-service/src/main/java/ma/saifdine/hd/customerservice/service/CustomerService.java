package ma.saifdine.hd.customerservice.service;

import ma.saifdine.hd.customerservice.dtos.CustomerRequestDTO;
import ma.saifdine.hd.customerservice.dtos.CustomerResponseDTO;
import ma.saifdine.hd.customerservice.dtos.CustomerStatsDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface CustomerService {

    CustomerResponseDTO createCustomer(CustomerRequestDTO dto);

    CustomerResponseDTO getCustomer(Long id);

    List<CustomerResponseDTO> getAllCustomers();

    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto);

    void deleteCustomer(Long id);

    Page<CustomerResponseDTO> searchCustomerByPagenation(int page, int size, String keyword);

    Page<CustomerResponseDTO> advancedSearch(
            String keyword,
            String city,
            String country,
            LocalDateTime createdAfter,
            LocalDateTime createdBefore,
            Boolean active,
            int page,
            int size
    );

    List<CustomerResponseDTO> getCustomersByCity(String city);

    List<CustomerResponseDTO> getCustomersByCountry(String country);

    CustomerResponseDTO restoreCustomer(Long id);

    void hardDeleteCustomer(Long id);

    List<CustomerResponseDTO> getInactiveCustomers();

    int restoreMultipleCustomers(List<Long> ids);

    int softDeleteMultipleCustomers(List<Long> ids);

    CustomerStatsDTO getStatistics();

    Map<String, Long> getCustomersCountByCountry();

    Map<String, Long> getCustomersCountByCity();

    CustomerStatsDTO getStatisticsBetween(LocalDateTime start, LocalDateTime end);

    List<CustomerResponseDTO> getTodayCustomers();

    boolean emailExists(String email);

    boolean isEmailUniqueForCustomer(String email, Long customerId);







}
