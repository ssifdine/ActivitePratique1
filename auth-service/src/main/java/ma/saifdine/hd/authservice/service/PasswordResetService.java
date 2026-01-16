package ma.saifdine.hd.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.saifdine.hd.authservice.entity.AuthCredential;
import ma.saifdine.hd.authservice.entity.PasswordResetToken;
import ma.saifdine.hd.authservice.exception.BadRequestException;
import ma.saifdine.hd.authservice.exception.UnauthorizedException;
import ma.saifdine.hd.authservice.repository.AuthCredentialRepository;
import ma.saifdine.hd.authservice.repository.PasswordResetTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final AuthCredentialRepository credentialRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public void initiatePasswordReset(String email) {
        // Rechercher l'utilisateur
        AuthCredential credential = credentialRepository.findByEmail(email)
                .orElse(null);

        // Pour des raisons de sécurité, ne pas révéler si l'email existe
        if (credential == null) {
            log.warn("Password reset requested for non-existent email: {}", email);
            // On ne lève pas d'exception pour éviter l'énumération des emails
            return;
        }

        // Vérifier que le compte est actif
        if (!credential.getIsActive()) {
            log.warn("Password reset requested for inactive account: {}", email);
            return;
        }

        // Supprimer les anciens tokens de cet utilisateur
        tokenRepository.deleteByUserId(credential.getUserId());

        // Générer un nouveau token sécurisé
        String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();

        // Créer et sauvegarder le token
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .userId(credential.getUserId())
                .email(credential.getEmail())
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        // Envoyer l'email
        try {
            emailService.sendPasswordResetEmail(
                    credential.getEmail(),
                    token,
                    credential.getEmail().split("@")[0] // Nom simple basé sur l'email
            );
            log.info("Password reset initiated for user: {}", credential.getUserId());
        } catch (Exception e) {
            log.error("Failed to send reset email: {}", e.getMessage());
            throw new RuntimeException("Failed to send reset email");
        }
    }

    @Transactional(readOnly = true)
    public void validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Token invalide"));

        if (resetToken.isUsed()) {
            throw new BadRequestException("Ce token a déjà été utilisé");
        }

        if (resetToken.isExpired()) {
            throw new BadRequestException("Ce token a expiré");
        }

        log.info("Token validated successfully for user: {}", resetToken.getUserId());
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Récupérer et valider le token
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("Token invalide"));

        if (resetToken.isUsed()) {
            throw new BadRequestException("Ce token a déjà été utilisé");
        }

        if (resetToken.isExpired()) {
            throw new BadRequestException("Ce token a expiré");
        }

        // Récupérer l'utilisateur
        AuthCredential credential = credentialRepository.findByUserId(resetToken.getUserId())
                .orElseThrow(() -> new UnauthorizedException("Utilisateur non trouvé"));

        // Vérifier que le compte est actif
        if (!credential.getIsActive()) {
            throw new UnauthorizedException("Compte désactivé");
        }

        // Mettre à jour le mot de passe
        credential.setPasswordHash(passwordEncoder.encode(newPassword));
        credential.setFailedLoginAttempts(0);
        credential.setLockedUntil(null);
        credentialRepository.save(credential);

        // Marquer le token comme utilisé
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password reset successfully for user: {}", credential.getUserId());
    }

    // Nettoyage automatique des tokens expirés (tous les jours à 2h du matin)
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Expired password reset tokens cleaned up");
    }
}