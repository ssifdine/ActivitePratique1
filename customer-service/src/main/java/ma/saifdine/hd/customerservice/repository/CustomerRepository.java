package ma.saifdine.hd.customerservice.repository;

import ma.saifdine.hd.customerservice.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.fullName LIKE :kw")
    Page<Customer> searchCustomerByPagenation(@Param("kw") String keyword, Pageable pageable);
}
