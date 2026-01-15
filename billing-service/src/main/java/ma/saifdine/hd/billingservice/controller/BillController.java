package ma.saifdine.hd.billingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.saifdine.hd.billingservice.dtos.*;
import ma.saifdine.hd.billingservice.dtos.response.ApiResponse;
import ma.saifdine.hd.billingservice.service.BillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
@Slf4j
public class BillController {

    private final BillService billService;

    /**
     * Créer une nouvelle facture
     * POST /api/bills
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<BillDetailDTO>> createBill(
            @Valid @RequestBody CreateBillDTO createDTO,
            Authentication authentication
    ) {
        log.info("Authenticated user: {}", authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Bill created successfully",
                        billService.createBill(createDTO)
                ));
    }


    /**
     * Récupérer toutes les factures
     * GET /api/bills
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<List<BillSummaryDTO>>> getAllBills() {
        log.info("REST request to get all bills");

        List<BillSummaryDTO> bills = billService.getAllBills();
        return ResponseEntity.ok(ApiResponse.success(bills));
    }

    /**
     * Récupérer une facture par ID
     * GET /api/bills/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<BillDetailDTO>> getBillById(@PathVariable Long id) {
        log.info("REST request to get bill: {}", id);

        BillDetailDTO bill = billService.getBillById(id);
        return ResponseEntity.ok(ApiResponse.success(bill));
    }

    /**
     * Récupérer les factures d'un client
     * GET /api/bills/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<List<BillSummaryDTO>>> getBillsByCustomerId(
            @PathVariable Long customerId) {
        log.info("REST request to get bills for customer: {}", customerId);

        List<BillSummaryDTO> bills = billService.getBillsByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success(bills));
    }

    /**
     * Mettre à jour une facture
     * PUT /api/bills
     */
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<BillDetailDTO>> updateBill(
            @Valid @RequestBody UpdateBillDTO updateDTO) {
        log.info("REST request to update bill: {}", updateDTO.getId());

        BillDetailDTO bill = billService.updateBill(updateDTO);
        return ResponseEntity.ok(ApiResponse.success("Bill updated successfully", bill));
    }

    /**
     * Supprimer une facture
     * DELETE /api/bills/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBill(@PathVariable Long id) {
        log.info("REST request to delete bill: {}", id);

        billService.deleteBill(id);
        return ResponseEntity.ok(ApiResponse.success("Bill deleted successfully", null));
    }

    /**
     * Obtenir les statistiques de facturation d'un client
     * GET /api/bills/customer/{customerId}/stats
     */
    @GetMapping("/customer/{customerId}/stats")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<CustomerBillStatsDTO>> getCustomerStats(
            @PathVariable Long customerId) {
        log.info("REST request to get bill stats for customer: {}", customerId);

        CustomerBillStatsDTO stats = billService.getCustomerBillStats(customerId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

}
