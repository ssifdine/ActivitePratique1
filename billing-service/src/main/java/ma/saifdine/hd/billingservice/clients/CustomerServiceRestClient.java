package ma.saifdine.hd.billingservice.clients;

import ma.saifdine.hd.billingservice.dtos.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service")
public interface CustomerServiceRestClient {

    @GetMapping("/api/customers/{id}")
    CustomerDTO findCustomerById(@PathVariable Long id);
}
