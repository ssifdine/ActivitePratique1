package ma.saifdine.hd.customerservice.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

    private String street;
    private String city;
    private String country;
    private String postalCode;
}
