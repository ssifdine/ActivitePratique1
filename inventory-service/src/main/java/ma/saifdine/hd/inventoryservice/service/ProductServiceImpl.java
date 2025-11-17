package ma.saifdine.hd.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import ma.saifdine.hd.inventoryservice.dtos.ProductDTO;
import ma.saifdine.hd.inventoryservice.entity.Product;
import ma.saifdine.hd.inventoryservice.exception.ProductNotFoundException;
import ma.saifdine.hd.inventoryservice.mapper.ProductMapper;
import ma.saifdine.hd.inventoryservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toDto(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductDTO updateProduct(Long id,ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());

        return productMapper.toDto(productRepository.save(product));
    }
}
