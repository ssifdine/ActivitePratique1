package ma.saifdine.hd.authservice.client;

import ma.saifdine.hd.authservice.dto.CreateUserRequest;
import ma.saifdine.hd.authservice.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceRestClient {

    @PostMapping("/api/users")
    ResponseEntity<UserResponseDTO> createUser(@RequestBody CreateUserRequest request);
}
