package ma.saifdine.hd.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${app.mail.from:noreply@system.com}")
    private String fromEmail;

    public void sendPasswordResetEmail(String toEmail, String token, String userName) {

        String resetLink = frontendUrl + "/reset-password?token=" + token;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail); // ✅ VALIDE
            message.setTo(toEmail);
            message.setSubject("Réinitialisation de votre mot de passe - App-MicroService");
            message.setText(buildEmailBody(userName, resetLink));

            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}", toEmail, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildEmailBody(String userName, String resetLink) {
        return String.format("""
        Bonjour %s,

        Vous avez demandé la réinitialisation de votre mot de passe sur App-MicroService.

        Cliquez sur le lien ci-dessous pour créer un nouveau mot de passe :
        %s

        Ce lien est valide pendant 1 heure uniquement.

        Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.

        Cordialement,
        L'équipe App-MicroService
        """,
                userName,
                resetLink
        );
    }

}
