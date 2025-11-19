package ma.saifdine.hd.billingservice.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerBillStatsDTO {
    private Long customerId;
    private Integer totalBills;
    private Double totalAmount;
    private Integer totalItems;
    private Double averageBillAmount;
}
