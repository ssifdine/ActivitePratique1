package ma.saifdine.hd.authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.saifdine.hd.authservice.dto.AuthResponse;
import ma.saifdine.hd.authservice.dto.LoginRequest;
import ma.saifdine.hd.authservice.dto.RefreshTokenRequest;
import ma.saifdine.hd.authservice.dto.RegisterRequest;
import ma.saifdine.hd.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}
