package ma.saifdine.hd.inventoryservice.mapper;

import ma.saifdine.hd.inventoryservice.dtos.ProductDTO;
import ma.saifdine.hd.inventoryservice.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toDto(Product product);

    Product toEntity(ProductDTO productDTO);

}
