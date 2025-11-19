package ma.saifdine.hd.billingservice.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO pour mettre à jour un article de facture
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateProductItemDTO {
    @NotNull
    private Long id;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
