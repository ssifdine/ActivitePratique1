package ma.saifdine.hd.customerservice.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CustomerResponseDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phone;

    private String street;
    private String city;
    private String country;
    private String postalCode;

    private boolean active;
}