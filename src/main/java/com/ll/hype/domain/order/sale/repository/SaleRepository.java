package com.ll.hype.domain.order.sale.repository;

import com.ll.hype.domain.order.sale.entity.Sale;
import com.ll.hype.domain.shoes.shoes.entity.Shoes;
import com.ll.hype.domain.shoes.shoes.entity.ShoesSize;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("SELECT s FROM Sale s " +
            "WHERE s.shoes.id = :#{#shoes.id} " +
            "AND (s.shoesSize, s.price) IN " +
            "(SELECT ss.shoesSize, MIN(ss.price) FROM Sale ss WHERE ss.shoes.id = :#{#shoes.id} GROUP BY ss.shoesSize) " +
            "ORDER BY s.shoesSize.size ASC")
    List<Sale> findLowestPriceByShoesId(Shoes shoes);

    @Query("SELECT s FROM Sale s " +
            "WHERE s.shoes = :shoes " +
            "AND s.shoesSize.size = :size " +
            "AND s.price = (SELECT MIN(ss.price) FROM Sale ss WHERE ss.shoes = :shoes AND ss.shoesSize.size = :size)")
    Sale findLowestPriceSale(@Param("shoes") Shoes shoes, @Param("size") int size);
}
