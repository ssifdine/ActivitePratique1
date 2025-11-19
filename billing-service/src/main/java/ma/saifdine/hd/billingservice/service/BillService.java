package ma.saifdine.hd.billingservice.service;

import ma.saifdine.hd.billingservice.dtos.*;

import java.util.List;

public interface BillService {

     BillDetailDTO createBill(CreateBillDTO createBillDTO);

     List<BillSummaryDTO> getAllBills();

     BillDetailDTO getBillById(Long id);

     List<BillSummaryDTO> getBillsByCustomerId(Long customerId);

     BillDetailDTO updateBill(UpdateBillDTO updateBillDTO);

     void deleteBill(Long id);

     CustomerBillStatsDTO getCustomerBillStats(Long customerId);
}
