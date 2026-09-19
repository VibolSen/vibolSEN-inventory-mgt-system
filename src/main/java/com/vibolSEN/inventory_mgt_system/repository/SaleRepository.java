package com.vibolSEN.inventory_mgt_system.repository;

import com.vibolSEN.inventory_mgt_system.model.Sale;
import com.vibolSEN.inventory_mgt_system.model.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    Optional<Sale> findBySaleNumberIgnoreCase(String saleNumber);

    boolean existsBySaleNumberIgnoreCase(String saleNumber);

    List<Sale> findByCustomerId(Long customerId);

    List<Sale> findByUserId(Long userId);

    List<Sale> findByPaymentStatus(PaymentStatus paymentStatus);

    Page<Sale> findByPaymentStatus(PaymentStatus paymentStatus, Pageable pageable);

    @Query("SELECT s FROM Sale s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    List<Sale> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Sale s WHERE " +
            "LOWER(s.saleNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.customerName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Sale> searchSales(@Param("keyword") String keyword);

    @Query("SELECT s FROM Sale s WHERE " +
            "LOWER(s.saleNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.customerName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Sale> searchSales(@Param("keyword") String keyword, Pageable pageable);
}
