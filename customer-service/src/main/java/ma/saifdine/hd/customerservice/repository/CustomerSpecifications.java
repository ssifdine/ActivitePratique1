package ma.saifdine.hd.customerservice.repository;

import jakarta.persistence.criteria.Predicate;
import ma.saifdine.hd.customerservice.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * Specifications JPA pour recherche dynamique complexe
 */
public class CustomerSpecifications {

    /**
     * Recherche par keyword (nom, email, téléphone)
     */
    public static Specification<Customer> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("fullName")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("phone")), pattern)
            );
        };
    }

    /**
     * Actif seulement
     */
    public static Specification<Customer> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }

    /**
     * Par ville
     */
    public static Specification<Customer> hasCity(String city) {
        return (root, query, cb) -> {
            if (city == null || city.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("address").get("city")), city.toLowerCase());
        };
    }

    /**
     * Par pays
     */
    public static Specification<Customer> hasCountry(String country) {
        return (root, query, cb) -> {
            if (country == null || country.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("address").get("country")), country.toLowerCase());
        };
    }

    /**
     * Créé après une date
     */
    public static Specification<Customer> createdAfter(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    /**
     * Créé avant une date
     */
    public static Specification<Customer> createdBefore(LocalDateTime date) {
        return (root, query, cb) -> {
            if (date == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("createdAt"), date);
        };
    }

    /**
     * Combinaison de critères
     */
    public static Specification<Customer> filterBy(
            String keyword,
            String city,
            String country,
            LocalDateTime createdAfter,
            LocalDateTime createdBefore,
            Boolean active
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Keyword
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("fullName")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("phone")), pattern)
                ));
            }

            // City
            if (city != null && !city.isBlank()) {
                predicates.add(cb.equal(
                        cb.lower(root.get("address").get("city")),
                        city.toLowerCase()
                ));
            }

            // Country
            if (country != null && !country.isBlank()) {
                predicates.add(cb.equal(
                        cb.lower(root.get("address").get("country")),
                        country.toLowerCase()
                ));
            }

            // Date range
            if (createdAfter != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
            }
            if (createdBefore != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdBefore));
            }

            // Active status
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
