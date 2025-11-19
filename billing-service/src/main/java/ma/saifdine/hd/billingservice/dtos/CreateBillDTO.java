package ma.saifdine.hd.billingservice.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

/**
 * DTO pour créer une nouvelle facture
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CreateBillDTO {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "Bill must contain at least one product")
    private List<CreateProductItemDTO> productItems;
}
