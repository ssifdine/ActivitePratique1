package ma.saifdine.hd.inventoryservice.service;

import ma.saifdine.hd.inventoryservice.dtos.ProductDTO;

import java.util.List;

public interface ProductService {

        ProductDTO addProduct(ProductDTO productDTO);

        ProductDTO getProductById(Long id);

        List<ProductDTO> getAllProducts();

        void deleteProduct(Long id);

        ProductDTO updateProduct(Long id,ProductDTO productDTO);




}
