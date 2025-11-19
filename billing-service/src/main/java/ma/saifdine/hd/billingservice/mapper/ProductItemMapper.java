package ma.saifdine.hd.billingservice.mapper;

import ma.saifdine.hd.billingservice.dtos.*;
import ma.saifdine.hd.billingservice.entity.ProductItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductItemMapper {

    /**
     * Mapper ProductItem -> ProductItemDTO
     * Pour afficher un article de facture
     */
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "product", target = "product")
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(productItem))")
    ProductItemDTO toDTO(ProductItem productItem);

    /**
     * Mapper CreateProductItemDTO -> ProductItem
     * Pour créer un nouvel article
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", ignore = true)  // Sera récupéré du service Product
    @Mapping(target = "bill", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductItem toEntity(CreateProductItemDTO dto);

    /**
     * Mapper pour mettre à jour un article existant
     */
    @Mapping(target = "productId", ignore = true)  // Ne pas changer le produit
    @Mapping(target = "price", ignore = true)  // Ne pas changer le prix
    @Mapping(target = "bill", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateEntityFromDTO(UpdateProductItemDTO dto, @MappingTarget ProductItem productItem);

    /**
     * Mapper une liste d'articles
     */
    List<ProductItemDTO> toDTOList(List<ProductItem> productItems);

    // ============= Méthodes Helper =============

    /**
     * Calcule le prix total d'un article (prix * quantité)
     */
    default Double calculateTotalPrice(ProductItem productItem) {
        if (productItem == null) {
            return 0.0;
        }
        return productItem.getPrice() * productItem.getQuantity();
    }
}