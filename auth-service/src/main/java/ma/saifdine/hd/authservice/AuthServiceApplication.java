package ma.saifdine.hd.authservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.saifdine.hd.authservice.client.UserServiceRestClient;
import ma.saifdine.hd.authservice.dto.CreateUserRequest;
import ma.saifdine.hd.authservice.entity.AuthCredential;
import ma.saifdine.hd.authservice.enums.Role;
import ma.saifdine.hd.authservice.repository.AuthCredentialRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@SpringBootApplication
@EnableFeignClients
@Slf4j
@RequiredArgsConstructor
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initAdmin(
            AuthCredentialRepository credentialRepository,
            PasswordEncoder passwordEncoder,
            UserServiceRestClient userServiceRestClient,
            @Value("${admin.email}") String adminEmail,
            @Value("${admin.password}") String adminPassword
    ) {
        return args -> {

            if (credentialRepository.existsByEmail(adminEmail)) {
                log.info("ADMIN already exists");
                return;
            }

            UUID adminId = UUID.randomUUID();

            AuthCredential admin = AuthCredential.builder()
                    .userId(adminId)
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .isActive(true)
                    .failedLoginAttempts(0)
                    .build();

            credentialRepository.save(admin);

            userServiceRestClient.createUser(
                    CreateUserRequest.builder()
                            .userId(adminId)
                            .email(adminEmail)
                            .firstName("Super")
                            .lastName("Admin")
                            .role(Role.ADMIN)
                            .build()
            );

            log.info("ADMIN created successfully with email {}", adminEmail);
        };
    }
}
