package ma.saifdine.hd.inventoryservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import ma.saifdine.hd.inventoryservice.exception.TokenExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaims(token).getSubject());
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public String extractEmail(String token) {
        return extractClaims(token).get("email", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            if (isTokenExpired(token)) {
                throw new TokenExpiredException();
            }
            return true;
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException(); // si jjwt détecte un token expiré automatiquement
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaims(token).getExpiration();
        return expiration.before(new Date());
    }


}
