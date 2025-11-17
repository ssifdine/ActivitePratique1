package ma.saifdine.hd.inventoryservice.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id; // facultatif pour création

    @NotBlank(message = "Le nom du produit est obligatoire")
    private String name;

    @NotBlank(message = "La description du produit est obligatoire")
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix doit être positif")
    private Double price;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 0, message = "La quantité doit être positive")
    private Integer quantity;
}
