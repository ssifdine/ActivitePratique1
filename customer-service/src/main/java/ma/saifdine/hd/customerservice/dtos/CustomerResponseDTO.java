package ma.saifdine.hd.customerservice.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CustomerResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String fullName;
    private String email;
    private String phone;

    private String street;
    private String city;
    private String country;
    private String postalCode;

    private boolean active;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}