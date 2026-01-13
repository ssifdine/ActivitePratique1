package ma.saifdine.hd.customerservice.service;

import lombok.RequiredArgsConstructor;
import ma.saifdine.hd.customerservice.dtos.CustomerRequestDTO;
import ma.saifdine.hd.customerservice.dtos.CustomerResponseDTO;
import ma.saifdine.hd.customerservice.entity.Customer;
import ma.saifdine.hd.customerservice.exception.CustomerNotFoundException;
import ma.saifdine.hd.customerservice.mapper.CustomerMapper;
import ma.saifdine.hd.customerservice.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper mapper;

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO dto) {
        // Vérifier si l'email existe déjà
        Optional<Customer> existing = customerRepository.findByEmail(dto.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        Customer customer = mapper.toEntity(dto);
        return mapper.toDTO(customerRepository.save(customer));
    }


    @Override
    public CustomerResponseDTO getCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return mapper.toDTO(customer);
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO dto) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        existing.setFullName(dto.getFullName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.getAddress().setStreet(dto.getStreet());
        existing.getAddress().setCity(dto.getCity());
        existing.getAddress().setCountry(dto.getCountry());
        existing.getAddress().setPostalCode(dto.getPostalCode());

        return mapper.toDTO(customerRepository.save(existing));
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }
        customerRepository.deleteById(id);
    }

    @Override
    public Page<CustomerResponseDTO> searchCustomerByPagenation(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository.searchCustomerByPagenation("%" + keyword + "%",pageable);
        return customerPage.map(mapper::toDTO);
    }
}