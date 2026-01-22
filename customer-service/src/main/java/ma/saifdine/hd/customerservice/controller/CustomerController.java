package ma.saifdine.hd.customerservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.saifdine.hd.customerservice.dtos.CustomerRequestDTO;
import ma.saifdine.hd.customerservice.dtos.CustomerResponseDTO;
import ma.saifdine.hd.customerservice.dtos.CustomerStatsDTO;
import ma.saifdine.hd.customerservice.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "PIs pour la gestion des clients")
@SecurityRequirement(name = "Bearer Authentication")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Créer un customer - Accessible par ADMIN
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Créer un nouveau client", description = "Accessible uniquement aux ADMIN")
    @ApiResponse(responseCode = "200" , description = "Client crée avec succés")
    @ApiResponse(responseCode = "400" , description = "Données invalides")
    @ApiResponse(responseCode = "403", description = "Accés refusé")
    @ApiResponse(responseCode = "409", description = "Email déja existant")
    public ResponseEntity<CustomerResponseDTO> create(
            @Valid @RequestBody CustomerRequestDTO dto,
            Authentication authentication) {

        UUID userId = (UUID) authentication.getPrincipal();
        log.info("User {} is creating a customer", userId);

        return ResponseEntity.ok(customerService.createCustomer(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Récupérer un client par ID")
    public ResponseEntity<CustomerResponseDTO> get(@Parameter(description = "ID du client") @PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Liste de tous les clients actifs")
    public ResponseEntity<List<CustomerResponseDTO>> getAll() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Recherche paginée de clients")
    public ResponseEntity<Page<CustomerResponseDTO>> searchPaginated(
            @Parameter(description = "Mot-clé de recherche")
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @Parameter(description = "Numéro de page (0-indexed)")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Taille de page")
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        int safeSize = Math.max(size, 1);
        Page<CustomerResponseDTO> customers = customerService
                .searchCustomerByPagenation(page, safeSize, keyword);
        return ResponseEntity.ok(customers);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Mettre a jour un client")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Long id,
                                                      @Valid @RequestBody CustomerRequestDTO dto) {
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Supprimer un client (soft delete)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/advanced-search")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Recherche avancée avec filtres multiples")
    public ResponseEntity<Page<CustomerResponseDTO>> advancedSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime createdBefore,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<CustomerResponseDTO> result = customerService.advancedSearch(
                keyword, city, country, createdAfter, createdBefore, active, page, size
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-city/{city}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Récupérer les clients d'une ville")
    public ResponseEntity<List<CustomerResponseDTO>> getByCity(@PathVariable String city) {
        return ResponseEntity.ok(customerService.getCustomersByCity(city));
    }

    @GetMapping("/by-country/{country}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Récupérer les clients d'un pays")
    public ResponseEntity<List<CustomerResponseDTO>> getByCountry(@PathVariable String country) {
        return ResponseEntity.ok(customerService.getCustomersByCountry(country));
    }

    @PatchMapping("/{id}/restore")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Restaurer un client soft deleted")
    public ResponseEntity<CustomerResponseDTO> restore(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.restoreCustomer(id));
    }

    @DeleteMapping("/{id}/hard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Suppression définitive (hard delete)",
            description = "ATTENTION: Cette action est irréversible!")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id) {
        customerService.hardDeleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Liste des clients soft deleted")
    public ResponseEntity<List<CustomerResponseDTO>> getDeleted() {
        return ResponseEntity.ok(customerService.getInactiveCustomers());
    }

    @PostMapping("/restore-multiple")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restaurer plusieurs clients")
    public ResponseEntity<Map<String, Integer>> restoreMultiple(
            @RequestBody List<Long> ids) {
        int count = customerService.restoreMultipleCustomers(ids);
        return ResponseEntity.ok(Map.of("restored", count));
    }

    @DeleteMapping("/soft-delete-multiple")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete multiple clients")
    public ResponseEntity<Map<String, Integer>> softDeleteMultiple(
            @RequestBody List<Long> ids) {
        int count = customerService.softDeleteMultipleCustomers(ids);
        return ResponseEntity.ok(Map.of("deleted", count));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Statistiques globales des clients")
    public ResponseEntity<CustomerStatsDTO> getStats() {
        return ResponseEntity.ok(customerService.getStatistics());
    }

    @GetMapping("/stats/by-country")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Nombre de clients par pays")
    public ResponseEntity<Map<String, Long>> getStatsByCountry() {
        return ResponseEntity.ok(customerService.getCustomersCountByCountry());
    }

    @GetMapping("/stats/by-city")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Nombre de clients par ville")
    public ResponseEntity<Map<String, Long>> getStatsByCity() {
        return ResponseEntity.ok(customerService.getCustomersCountByCity());
    }

    @GetMapping("/stats/period")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Statistiques sur une période")
    public ResponseEntity<CustomerStatsDTO> getStatsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(customerService.getStatisticsBetween(start, end));
    }

    @GetMapping("/today")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Nouveaux clients du jour")
    public ResponseEntity<List<CustomerResponseDTO>> getTodayCustomers() {
        return ResponseEntity.ok(customerService.getTodayCustomers());
    }

    @GetMapping("/check-email")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Vérifier si un email existe")
    public ResponseEntity<Map<String, Boolean>> checkEmail(
            @RequestParam String email) {
        boolean exists = customerService.emailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/validate-unique-email")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Valider email unique pour update")
    public ResponseEntity<Map<String, Boolean>> validateUniqueEmail(
            @RequestParam String email,
            @RequestParam Long customerId) {
        boolean isUnique = customerService.isEmailUniqueForCustomer(email, customerId);
        return ResponseEntity.ok(Map.of("isUnique", isUnique));
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exporter les clients en CSV")
    public ResponseEntity<byte[]> exportCsv() {
        // TODO: Implémenter export CSV
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exporter les clients en Excel")
    public ResponseEntity<byte[]> exportExcel() {
        // TODO: Implémenter export Excel
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/health")
    @Operation(summary = "Health check du service customer")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "customer-service",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
