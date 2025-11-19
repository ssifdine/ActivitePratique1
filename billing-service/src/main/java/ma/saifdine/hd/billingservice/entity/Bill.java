package ma.saifdine.hd.billingservice.entity;

import jakarta.persistence.*;
import lombok.*;
import ma.saifdine.hd.billingservice.dtos.CustomerDTO;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date billingDate;
    private long customerId;
    @OneToMany(
            mappedBy = "bill",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductItem> productItems;
    @Transient
    private CustomerDTO customer;
}
