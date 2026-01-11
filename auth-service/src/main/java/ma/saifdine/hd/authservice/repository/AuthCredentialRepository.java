package ma.saifdine.hd.authservice.repository;

import ma.saifdine.hd.authservice.entity.AuthCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthCredentialRepository extends JpaRepository<AuthCredential, UUID> {

    Optional<AuthCredential> findByEmail(String email);

    Optional<AuthCredential> findByUserId(UUID userId);

    boolean existsByEmail(String email);
}
