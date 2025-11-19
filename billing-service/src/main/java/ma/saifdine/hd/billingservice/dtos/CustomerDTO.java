package ma.saifdine.hd.billingservice.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CustomerDTO {

    private Long id;
    private String fullName;
    private String email;

}
