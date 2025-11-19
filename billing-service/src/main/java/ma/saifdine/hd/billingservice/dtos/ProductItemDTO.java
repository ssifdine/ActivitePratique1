package ma.saifdine.hd.billingservice.dtos;

import lombok.*;

/**
 * DTO pour afficher un article de facture
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductItemDTO {
    private Long id;
    private Long productId;
    private ProductDTO product;
    private Integer quantity;
    private Double price;
    private Double totalPrice;  // quantity * price
}
