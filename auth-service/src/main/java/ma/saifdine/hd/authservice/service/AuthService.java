package ma.saifdine.hd.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.saifdine.hd.authservice.client.UserServiceRestClient;
import ma.saifdine.hd.authservice.dto.AuthResponse;
import ma.saifdine.hd.authservice.dto.CreateUserRequest;
import ma.saifdine.hd.authservice.dto.LoginRequest;
import ma.saifdine.hd.authservice.dto.RegisterRequest;
import ma.saifdine.hd.authservice.entity.AuthCredential;
import ma.saifdine.hd.authservice.entity.RefreshToken;
import ma.saifdine.hd.authservice.enums.Role;
import ma.saifdine.hd.authservice.exception.BadRequestException;
import ma.saifdine.hd.authservice.exception.UnauthorizedException;
import ma.saifdine.hd.authservice.repository.AuthCredentialRepository;
import ma.saifdine.hd.authservice.repository.RefreshTokenRepository;
import ma.saifdine.hd.authservice.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthCredentialRepository credentialRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserServiceRestClient userServiceRestClient;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Transactional
    public Map register(RegisterRequest request) {
        if (credentialRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        UUID userId = UUID.randomUUID();

        AuthCredential credential = AuthCredential.builder()
                .userId(userId)
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .isActive(true)
                .failedLoginAttempts(0)
                .build();

        credentialRepository.save(credential);

        try {

            CreateUserRequest userRequest = CreateUserRequest.builder()
                    .email(request.getEmail())
                    .userId(userId)
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .build();

            userServiceRestClient.createUser(userRequest);
            log.info("User profile created successfully for userId: {}", userId);
        }catch (Exception e) {
            log.error("Failed to create user profile: {}", e.getMessage());
            throw new RuntimeException("Failed to create user profile: " + e.getMessage());
        }
        Map response = new HashMap<>();
        response.put("message", "User created successfully");
        response.put("userId", userId);
        return response;
    }

    @Transactional
    public AuthResponse refreshAccessToken(String refreshToken) {
        // Valider le format et l'expiration du refresh token
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        // Extraire l'userId du refresh token
        UUID userId = jwtService.extractUserId(refreshToken);

        // Vérifier que le refresh token existe en base et n'est pas révoqué
        RefreshToken storedToken = refreshTokenRepository
                .findByUserIdAndTokenHashAndRevokedFalse(userId, refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found or revoked"));

        // Vérifier que le token n'est pas expiré en base
        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token has expired");
        }

        // Récupérer les credentials de l'utilisateur
        AuthCredential credential = credentialRepository
                .findByUserId(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Vérifier que le compte est actif
        if (!credential.getIsActive()) {
            throw new UnauthorizedException("Account is disabled");
        }

        // Générer un nouveau access token
        String newAccessToken = jwtService.generateAccessToken(credential);

        // Optionnel : Générer un nouveau refresh token (rotation)
//         String newRefreshToken = jwtService.generateRefreshToken(userId);
//         storedToken.setRevoked(true);
//         refreshTokenRepository.save(storedToken);
//         saveRefreshToken(userId, newRefreshToken);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken) // ou newRefreshToken si rotation
                .expiresIn(expiration)
                .role(credential.getRole().name())
                .userId(credential.getUserId())
                .email(credential.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        AuthCredential credential = credentialRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (credential.getLockedUntil() != null &&
                credential.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new UnauthorizedException("Account is locked. Try again later.");
        }

        if (!passwordEncoder.matches(request.getPassword(), credential.getPasswordHash())) {
            handleFailedLogin(credential);
            throw new UnauthorizedException("Invalid credentials");
        }

        if (!credential.getIsActive()) {
            throw new UnauthorizedException("Account is disabled");
        }

        // Reset failed attempts
        credential.setFailedLoginAttempts(0);
        credential.setLockedUntil(null);
        credentialRepository.save(credential);

        String accessToken = jwtService.generateAccessToken(credential);
        String refreshToken = jwtService.generateRefreshToken(credential.getUserId());

        saveRefreshToken(credential.getUserId(), refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiration)
                .role(credential.getRole().name())
                .userId(credential.getUserId())
                .email(credential.getEmail())
                .build();
    }

    private void handleFailedLogin(AuthCredential credential) {
        int attempts = credential.getFailedLoginAttempts() + 1;
        credential.setFailedLoginAttempts(attempts);

        if (attempts >= 5) {
            credential.setLockedUntil(LocalDateTime.now().plusMinutes(30));
        }

        credentialRepository.save(credential);
    }

    private void saveRefreshToken(UUID userId, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .tokenHash(token)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        UUID userId = jwtService.extractUserId(refreshToken);

        RefreshToken token = refreshTokenRepository
                .findByUserIdAndTokenHashAndRevokedFalse(userId, refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        token.setRevoked(true);
        refreshTokenRepository.save(token);

        log.info("User {} logged out successfully", userId);
    }

}
