package ma.saifdine.hd.billingservice.dtos;

import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * DTO détaillé pour afficher UNE facture complète
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BillDetailDTO {
    private Long id;
    private Date billingDate;
    private Long customerId;
    private CustomerDTO customer;
    private List<ProductItemDTO> productItems;
    private Double subtotal;
    private Double tax;
    private Double totalAmount;
}
