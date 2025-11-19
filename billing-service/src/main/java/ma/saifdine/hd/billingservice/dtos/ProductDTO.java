package ma.saifdine.hd.billingservice.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private double price;
    private int quantity;
}
