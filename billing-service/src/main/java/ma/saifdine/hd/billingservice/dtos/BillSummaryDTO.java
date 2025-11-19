package ma.saifdine.hd.billingservice.dtos;

import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * DTO résumé pour afficher une liste de factures
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BillSummaryDTO {
    private Long id;
    private Date billingDate;
    private Long customerId;
    private String customerName;
    private Integer itemCount;
    private Double totalAmount;
}
