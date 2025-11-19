package ma.saifdine.hd.billingservice.mapper;

import ma.saifdine.hd.billingservice.dtos.*;
import ma.saifdine.hd.billingservice.entity.Bill;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {ProductItemMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BillMapper {

    /**
     * Mapper Bill -> BillSummaryDTO
     * Pour afficher une liste de factures
     */
    @Mapping(target = "customerName", source = "customer.fullName")
    @Mapping(target = "itemCount", expression = "java(calculateItemCount(bill))")
    @Mapping(target = "totalAmount", expression = "java(calculateTotalAmount(bill))")
    BillSummaryDTO toSummaryDTO(Bill bill);

    /**
     * Mapper Bill -> BillDetailDTO
     * Pour afficher une facture complète avec tous les détails
     */
    @Mapping(source = "customerId", target = "customerId")
    @Mapping(source = "customer", target = "customer")
    @Mapping(source = "productItems", target = "productItems")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(bill))")
    @Mapping(target = "tax", expression = "java(calculateTax(bill))")
    @Mapping(target = "totalAmount", expression = "java(calculateTotalAmount(bill))")
    BillDetailDTO toDetailDTO(Bill bill);

    /**
     * Mapper CreateBillDTO -> Bill
     * Pour créer une nouvelle facture
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "billingDate", expression = "java(new java.util.Date())")
    @Mapping(target = "productItems", ignore = true)  // Sera géré par le service
    @Mapping(target = "customer", ignore = true)
    Bill toEntity(CreateBillDTO dto);

    /**
     * Mapper pour mettre à jour une facture existante
     */
    @Mapping(target = "customerId", ignore = true)  // Ne pas changer le client
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "productItems", ignore = true)
    void updateEntityFromDTO(UpdateBillDTO dto, @MappingTarget Bill bill);

    /**
     * Mapper une liste de factures
     */
    List<BillSummaryDTO> toSummaryDTOList(List<Bill> bills);

    List<BillDetailDTO> toDetailDTOList(List<Bill> bills);

    // ============= Méthodes Helper =============

    /**
     * Calcule le nombre total d'articles dans la facture
     */
    default Integer calculateItemCount(Bill bill) {
        if (bill.getProductItems() == null) {
            return 0;
        }
        return bill.getProductItems().stream()
                .mapToInt(item -> item.getQuantity())
                .sum();
    }

    /**
     * Calcule le sous-total (somme des prix * quantités)
     */
    default Double calculateSubtotal(Bill bill) {
        if (bill.getProductItems() == null) {
            return 0.0;
        }
        return bill.getProductItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    /**
     * Calcule la taxe (exemple: 20% du sous-total)
     */
    default Double calculateTax(Bill bill) {
        Double subtotal = calculateSubtotal(bill);
        return subtotal * 0.20;  // 20% de taxe
    }

    /**
     * Calcule le montant total (sous-total + taxe)
     */
    default Double calculateTotalAmount(Bill bill) {
        Double subtotal = calculateSubtotal(bill);
        Double tax = calculateTax(bill);
        return subtotal + tax;
    }
}