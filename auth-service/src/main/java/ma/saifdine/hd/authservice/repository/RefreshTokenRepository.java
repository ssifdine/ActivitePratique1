package ma.saifdine.hd.authservice.repository;

import ma.saifdine.hd.authservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByUserIdAndTokenHashAndRevokedFalse(UUID userId, String tokenHash);

    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findByUserIdAndRevokedFalse(UUID userId);

    void deleteByUserId(UUID userId);
}
