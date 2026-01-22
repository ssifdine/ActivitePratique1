package ma.saifdine.hd.customerservice.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerStatsDTO {
    private long totalCustomers;
    private long activeCustomers;
    private long inactiveCustomers;
    private long newCustomersToday;
    private long newCustomersThisWeek;
    private long newCustomersThisMonth;
    private double activePercentage;
}
