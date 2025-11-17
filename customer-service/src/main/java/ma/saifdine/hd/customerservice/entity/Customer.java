package ma.saifdine.hd.customerservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String firstName;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String lastName;

    @Email(message = "Email non valide")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(min = 8, max = 20)
    private String phone;

    @Embedded
    private Address address;

    @Column(nullable = false)
    private boolean active = true;
}
