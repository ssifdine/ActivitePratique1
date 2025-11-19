package ma.saifdine.hd.billingservice.repository;

import ma.saifdine.hd.billingservice.entity.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {

    List<ProductItem> findByBillId(Long billId);

}
