package ma.saifdine.hd.customerservice.repository;

import ma.saifdine.hd.customerservice.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    // ============================================
    // Requêtes de base
    // ============================================

    /**
     * Recherche par email
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Recherche par email et active
     */
    Optional<Customer> findByEmailAndActiveTrue(String email);

    /**
     * Tous les clients actifs
     */
    List<Customer> findByActiveTrue();

    /**
     * Tous les clients inactifs (soft deleted)
     */
    List<Customer> findByActiveFalse();

    // ============================================
    // Recherche avancée
    // ============================================

    /**
     * Recherche paginée par nom (LIKE)
     */
    @Query("SELECT c FROM Customer c WHERE c.fullName LIKE :kw AND c.active = true")
    Page<Customer> searchCustomerByPagenation(@Param("kw") String keyword, Pageable pageable);

    /**
     * Recherche avancée avec plusieurs critères
     */
    @Query("""
        SELECT c FROM Customer c 
        WHERE c.active = true
        AND (
            LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.address.city) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.address.country) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    Page<Customer> advancedSearch(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Recherche par ville
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.address.city) = LOWER(:city) AND c.active = true")
    List<Customer> findByCity(@Param("city") String city);

    /**
     * Recherche par pays
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.address.country) = LOWER(:country) AND c.active = true")
    List<Customer> findByCountry(@Param("country") String country);

    /**
     * Compter les clients par pays
     */
    @Query("SELECT c.address.country, COUNT(c) FROM Customer c WHERE c.active = true GROUP BY c.address.country")
    List<Object[]> countCustomersByCountry();

    /**
     * Compter les clients par ville
     */
    @Query("SELECT c.address.city, COUNT(c) FROM Customer c WHERE c.active = true GROUP BY c.address.city")
    List<Object[]> countCustomersByCity();

    // ============================================
    // Requêtes par date
    // ============================================

    /**
     * Clients créés après une date
     */
    @Query("SELECT c FROM Customer c WHERE c.createdAt >= :date AND c.active = true")
    List<Customer> findCustomersCreatedAfter(@Param("date") LocalDateTime date);

    /**
     * Clients modifiés entre deux dates
     */
    @Query("SELECT c FROM Customer c WHERE c.updatedAt BETWEEN :start AND :end AND c.active = true")
    List<Customer> findCustomersUpdatedBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Nouveaux clients du jour
     */
    @Query("SELECT c FROM Customer c WHERE DATE(c.createdAt) = CURRENT_DATE AND c.active = true")
    List<Customer> findTodayCustomers();

    // ============================================
    // Statistiques
    // ============================================

    /**
     * Compter les clients actifs
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.active = true")
    long countActiveCustomers();

    /**
     * Compter les clients inactifs
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.active = false")
    long countInactiveCustomers();

    /**
     * Statistiques par période
     */
    @Query("""
        SELECT 
            COUNT(c) as total,
            COUNT(CASE WHEN c.active = true THEN 1 END) as active,
            COUNT(CASE WHEN c.active = false THEN 1 END) as inactive
        FROM Customer c
        WHERE c.createdAt BETWEEN :start AND :end
    """)
    Object getStatsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // ============================================
    // Opérations batch
    // ============================================

    /**
     * Soft delete multiple
     */
    @Modifying
    @Query("UPDATE Customer c SET c.active = false WHERE c.id IN :ids")
    int softDeleteMultiple(@Param("ids") List<Long> ids);

    /**
     * Restaurer multiple
     */
    @Modifying
    @Query("UPDATE Customer c SET c.active = true WHERE c.id IN :ids")
    int restoreMultiple(@Param("ids") List<Long> ids);

    /**
     * Mise à jour de ville pour un pays
     */
    @Modifying
    @Query("UPDATE Customer c SET c.address.city = :newCity WHERE c.address.country = :country")
    int updateCityByCountry(@Param("country") String country, @Param("newCity") String newCity);

    // ============================================
    // Vérifications
    // ============================================

    /**
     * Vérifier si email existe
     */
    boolean existsByEmail(String email);

    /**
     * Vérifier si email existe pour un autre customer
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c WHERE c.email = :email AND c.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);

    /**
     * Vérifier si customer actif existe
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c WHERE c.id = :id AND c.active = true")
    boolean existsByIdAndActiveTrue(@Param("id") Long id);



}
