package ma.saifdine.hd.billingservice.clients;

import ma.saifdine.hd.billingservice.dtos.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("inventory-service")
public interface InventoryServiceRestClient {

    @GetMapping("/api/products/{id}")
    ProductDTO getProduct(@PathVariable Long id);

    @PutMapping("/api/products/{id}/quantity")
    ProductDTO updateProductQuantity(@PathVariable Long id,@RequestParam Integer quantity);
}
