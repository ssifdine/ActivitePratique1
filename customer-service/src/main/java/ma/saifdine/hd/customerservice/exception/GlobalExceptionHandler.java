package ma.saifdine.hd.customerservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<?> handleCustomerNotFound(CustomerNotFoundException e) {
        log.error("Customer not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "message", e.getMessage()
                ));
    }

    @ExceptionHandler({ AuthorizationDeniedException.class, AccessDeniedException.class })
    public ResponseEntity<?> handleSecurityException(Exception ex) {
        log.warn("Security error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "message", "Access Denied"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "message", "Internal Server Error"
                ));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> handleTokenExpired(TokenExpiredException ex) {
        log.warn("JWT token expired: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401 car le token n’est plus valide
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(Map.of(
                "timestamp", LocalDateTime.now(),
                "errors", errors
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }



}
