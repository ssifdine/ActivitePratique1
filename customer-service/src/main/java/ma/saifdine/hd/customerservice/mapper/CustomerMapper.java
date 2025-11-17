package ma.saifdine.hd.customerservice.mapper;

import ma.saifdine.hd.customerservice.dtos.CustomerRequestDTO;
import ma.saifdine.hd.customerservice.dtos.CustomerResponseDTO;
import ma.saifdine.hd.customerservice.entity.Address;
import ma.saifdine.hd.customerservice.entity.Customer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "address", expression = "java(mapAddress(dto))")
    Customer toEntity(CustomerRequestDTO dto);

    @Mapping(target = "fullName", expression = "java(customer.getFirstName() + \" \" + customer.getLastName())")
    @Mapping(target = "street", source = "address.street")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "country", source = "address.country")
    @Mapping(target = "postalCode", source = "address.postalCode")
    CustomerResponseDTO toDTO(Customer customer);

    default Address mapAddress(CustomerRequestDTO dto) {
        return Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .country(dto.getCountry())
                .postalCode(dto.getPostalCode())
                .build();
    }
}
