package ma.saifdine.hd.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.saifdine.hd.authservice.dto.*;
import ma.saifdine.hd.authservice.service.AuthService;
import ma.saifdine.hd.authservice.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;


    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshAccessToken(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@Valid @RequestBody RefreshTokenRequest response) {
        authService.logout(response.getRefreshToken());
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // ============= PASSWORD RESET ENDPOINTS =============

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.initiatePasswordReset(request.getEmail());
            // Message générique pour éviter l'énumération des emails
            return ResponseEntity.ok(MessageResponse.builder()
                    .message("Si cet email existe, un lien de réinitialisation a été envoyé")
                    .success(true)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(MessageResponse.builder()
                    .message("Si cet email existe, un lien de réinitialisation a été envoyé")
                    .success(true)
                    .build());
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<MessageResponse> validateToken(@RequestParam String token) {
        try {
            passwordResetService.validateToken(token);
            return ResponseEntity.ok(MessageResponse.builder()
                    .message("Token valide")
                    .success(true)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MessageResponse.builder()
                    .message(e.getMessage())
                    .success(false)
                    .build());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(MessageResponse.builder()
                    .message("Mot de passe réinitialisé avec succès")
                    .success(true)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MessageResponse.builder()
                    .message(e.getMessage())
                    .success(false)
                    .build());
        }
    }
}
