package ma.saifdine.hd.customerservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CustomerRequestDTO {

    @NotBlank
    @Size(min = 2)
    private String fullName;

    @Email
    private String email;

    @NotBlank
    private String phone;

    private String street;
    private String city;
    private String country;
    private String postalCode;
}
