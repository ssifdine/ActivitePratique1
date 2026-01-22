package ma.saifdine.hd.customerservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entité Customer avec fonctionnalités avancées:
 * - Audit automatique (création/modification)
 * - Soft delete
 * - Timestamps
 * - Serializable pour cache Redis
 */
@Entity
@Table(
        name = "customers",
        indexes = {
                @Index(name = "idx_customer_email", columnList = "email"),
                @Index(name = "idx_customer_fullname", columnList = "fullName"),
                @Index(name = "idx_customer_active", columnList = "active")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom complet est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Column(nullable = false, length = 100)
    private String fullName;

    @Email(message = "Email non valide")
    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Size(min = 8, max = 20, message = "Le téléphone doit contenir entre 8 et 20 caractères")
    @Column(nullable = false, length = 20)
    private String phone;

    @Embedded
    private Address address;

    /**
     * Soft delete: false = supprimé logiquement
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    /**
     * Date de création automatique
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date de dernière modification automatique
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * ID de l'utilisateur créateur (UUID)
     */
    @CreatedBy
    @Column(updatable = false, length = 36)
    private String createdBy;

    /**
     * ID du dernier utilisateur modificateur (UUID)
     */
    @LastModifiedBy
    @Column(length = 36)
    private String lastModifiedBy;

    /**
     * Version pour gestion optimiste des conflits
     */
    @Version
    private Long version;

    /**
     * Métadonnées supplémentaires (JSON)
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    /**
     * Méthode pour soft delete
     */
    public void softDelete() {
        this.active = false;
    }

    /**
     * Méthode pour restaurer
     */
    public void restore() {
        this.active = true;
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}