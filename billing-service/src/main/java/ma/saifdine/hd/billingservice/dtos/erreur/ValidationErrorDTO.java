package ma.saifdine.hd.billingservice.dtos.erreur;

import lombok.*;

/**
 * DTO pour les erreurs de validation
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ValidationErrorDTO {
    private String field;
    private String message;
    private Object rejectedValue;
}