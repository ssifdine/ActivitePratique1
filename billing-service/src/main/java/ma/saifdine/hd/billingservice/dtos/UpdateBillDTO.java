package ma.saifdine.hd.billingservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * DTO pour mettre à jour une facture
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateBillDTO {
    @NotNull
    private Long id;

    private Date billingDate;
    private List<UpdateProductItemDTO> productItems;
}
