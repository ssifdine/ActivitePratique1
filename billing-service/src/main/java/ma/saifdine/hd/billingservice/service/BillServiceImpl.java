package ma.saifdine.hd.billingservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.saifdine.hd.billingservice.clients.CustomerServiceRestClient;
import ma.saifdine.hd.billingservice.clients.InventoryServiceRestClient;
import ma.saifdine.hd.billingservice.dtos.*;
import ma.saifdine.hd.billingservice.entity.Bill;
import ma.saifdine.hd.billingservice.entity.ProductItem;
import ma.saifdine.hd.billingservice.exception.*;
import ma.saifdine.hd.billingservice.mapper.BillMapper;
import ma.saifdine.hd.billingservice.mapper.ProductItemMapper;
import ma.saifdine.hd.billingservice.repository.BillRepository;
import ma.saifdine.hd.billingservice.repository.ProductItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final ProductItemRepository productItemRepository;
    private final BillMapper billMapper;
    private final ProductItemMapper productItemMapper;
    private final CustomerServiceRestClient customerServiceRestClient;
    private final InventoryServiceRestClient inventoryServiceRestClient;


    @Override
    public BillDetailDTO createBill(CreateBillDTO createBillDTO) {
        // 1 - Verifier que le client existe
        CustomerDTO customerDTO;
        try {
            customerDTO = customerServiceRestClient
                    .findCustomerById(createBillDTO.getCustomerId());
        } catch (FeignException.NotFound e) {
            throw new CustomerNotFoundException(createBillDTO.getCustomerId());
        } catch (FeignException.Forbidden e) {
            throw new RuntimeException("Access denied to Customer Service");
        } catch (FeignException e) {
            throw new RuntimeException(
                    "Error calling Customer Service: " + e.contentUTF8()
            );
        }


        // 2 - Mapper CreateBillDTO --> Bill (MapStruct)
        Bill bill = billMapper.toEntity(createBillDTO);
        bill.setBillingDate(new Date());

        // 3 - Traiter les articles
        List<ProductItem> productItems = new ArrayList<>();

        for (CreateProductItemDTO itemDTO : createBillDTO.getProductItems()) {
            // Recuperer les infos du produit depuis le service Product
            ProductDTO productDTO;
            try {
                productDTO = inventoryServiceRestClient.getProduct(itemDTO.getProductId());
            } catch (Exception e) {
                throw new ProductNotFoundException(itemDTO.getProductId());
            }
            // Verifier le Stock
            if(productDTO.getQuantity() < itemDTO.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for productDTO: " + productDTO.getName() +
                                ". Available: " + productDTO.getQuantity() +
                                ", Requested: " + itemDTO.getQuantity()
                );
            }

            // Mapper CreateProductItemDTO --> ProductIem ( MapStruct)
            ProductItem productItem = productItemMapper.toEntity(itemDTO);

            // Definir les valeurs depuis le service Product
            productItem.setProductId(productDTO.getId());
            productItem.setPrice(productDTO.getPrice());  // Prix du PRODUIT, pas du DTO
            productItem.setBill(bill);
            productItem.setProduct(productDTO);  // Pour le DTO de retour

            productItems.add(productItem);

            // Reduire le stock ( appel au service Product)
            inventoryServiceRestClient.updateProductQuantity(
                    productDTO.getId(),
                    productDTO.getQuantity() - itemDTO.getQuantity()
            );
        }

        bill.setProductItems(productItems);

        // 4 - Sauvegarder la facture
        Bill savedBill = billRepository.save(bill);
        savedBill.setCustomer(customerDTO);

        // 5 - Mapper Bill --> BillDetailDTO ( MApStruct fait tout le travail)
        BillDetailDTO result = billMapper.toDetailDTO(savedBill);


        return result;
    }

    /**
     * Récupérer toutes les factures (résumé)
     */
    @Override
    @Transactional(readOnly = true)
    public List<BillSummaryDTO> getAllBills() {
        List<Bill> bills = billRepository.findAll();

        // Enrichir avec les infos client
        bills.forEach(bill -> {
            try {
                CustomerDTO customerDTO = customerServiceRestClient.findCustomerById(bill.getCustomerId());
                bill.setCustomer(customerDTO);
            }catch (Exception e) {
                log.error("Error fetching customer for bill {}: {}", bill.getId(), e.getMessage());
            }
        });

        // MapStruct mappe toute la liste
        return billMapper.toSummaryDTOList(bills);
    }

    /**
     * Récupérer une facture par ID (détaillée)
     */
    @Transactional(readOnly = true)
    @Override
    public BillDetailDTO getBillById(Long id) {
        log.info("Fetching bill with id: {}", id);

        Bill bill = billRepository.findById(id)
                .orElseThrow(()-> new BillNotFoundException(id));

        // Enrichir avec les infos client
        CustomerDTO customer = customerServiceRestClient.findCustomerById(bill.getCustomerId());
        bill.setCustomer(customer);

        // Enrichir avec les infos produits
        bill.getProductItems().forEach(productItem -> {
            try {
                ProductDTO product = inventoryServiceRestClient.getProduct(productItem.getProductId());
                productItem.setProduct(product);
            }catch (Exception e) {
                log.error("Error fetching product for item {}: {}", productItem.getId(), e.getMessage());
            }
        });
        // MapStruct mappe toute la structure
        return billMapper.toDetailDTO(bill);
    }

    /**
     * Récupérer les factures d'un client
     */
    @Transactional(readOnly = true)
    @Override
    public List<BillSummaryDTO> getBillsByCustomerId(Long customerId) {
        log.info("Fetching bills for customer: {}", customerId);

        // Verifier que le client existe
        CustomerDTO customerDTO;
        try {
            customerDTO = customerServiceRestClient.findCustomerById(customerId);
        } catch (Exception e) {
            throw new CustomerNotFoundException(customerId);
        }

        List<Bill> bills = billRepository.findByCustomerId(customerId);
        bills.forEach(bill -> {
            bill.setCustomer(customerDTO);
        });

        return billMapper.toSummaryDTOList(bills);
    }

    /**
     * Mettre à jour une facture
     */
    @Override
    public BillDetailDTO updateBill(UpdateBillDTO updateBillDTO) {
        log.info("Updating bill with id: {}", updateBillDTO.getId());

        Bill bill = billRepository.findById(updateBillDTO.getId())
                .orElseThrow(()-> new BillNotFoundException(updateBillDTO.getId()));

        // MapStruct met a jour l'entite
        billMapper.updateEntityFromDTO(updateBillDTO, bill);

        // Si on met a jour les articles
        if(updateBillDTO.getProductItems() != null && !updateBillDTO.getProductItems().isEmpty()) {
            for (UpdateProductItemDTO updateProductItemDTO : updateBillDTO.getProductItems()) {
                ProductItem item = productItemRepository.findById(updateProductItemDTO.getId())
                        .orElseThrow(() -> new ProductItemNotFoundException("Product item not found with id: " + updateProductItemDTO.getId()));

                // MapStruct met a jour l'article
                productItemMapper.updateEntityFromDTO(updateProductItemDTO, item);
            }
        }

        Bill savedBill = billRepository.save(bill);

        // Enrichir pour le mapping
        CustomerDTO customer = customerServiceRestClient.findCustomerById(savedBill.getCustomerId());
        savedBill.setCustomer(customer);

        savedBill.getProductItems().forEach(productItem -> {
            ProductDTO productDTO = inventoryServiceRestClient.getProduct(productItem.getProductId());
            productItem.setProduct(productDTO);
        });

        return billMapper.toDetailDTO(savedBill);
    }

    /**
     * Supprimer une facture
     */
    @Override
    public void deleteBill(Long id) {
        log.info("Deleting bill with id: {}", id);

        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new BillNotFoundException(id));

        // Restaurer le stock des produits
        bill.getProductItems().forEach(productItem -> {
            try {
                ProductDTO product = inventoryServiceRestClient.getProduct(productItem.getProductId());
                inventoryServiceRestClient.updateProductQuantity(
                        product.getId(),
                        product.getQuantity() + productItem.getQuantity()
                );
            } catch (Exception e) {
                log.error("Error restoring stock for product {}: {}", productItem.getProductId(), e.getMessage());
            }
        });

        billRepository.delete(bill);
        log.info("Bill deleted successfully: {}", id);
    }

    /**
     * Calculer les statistiques d'un client
     */
    @Transactional(readOnly = true)
    @Override
    public CustomerBillStatsDTO getCustomerBillStats(Long customerId) {
        log.info("Calculating bill stats for customer: {}", customerId);

        List<Bill> bills = billRepository.findByCustomerId(customerId);

        double totalAmount = bills.stream()
                .mapToDouble(bill -> billMapper.calculateTotalAmount(bill))
                .sum();

        int totalItems = bills.stream()
                .mapToInt(bill -> billMapper.calculateItemCount(bill))
                .sum();
        return CustomerBillStatsDTO.builder()
                .customerId(customerId)
                .totalBills(bills.size())
                .totalAmount(totalAmount)
                .totalItems(totalItems)
                .averageBillAmount(bills.isEmpty() ? 0.0 : totalAmount / bills.size())
                .build();
    }
}
