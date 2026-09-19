package com.vibolSEN.inventory_mgt_system.repository;

import com.vibolSEN.inventory_mgt_system.model.StockTransaction;
import com.vibolSEN.inventory_mgt_system.model.enums.StockTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findByProductId(Long productId);

    Page<StockTransaction> findByProductId(Long productId, Pageable pageable);

    List<StockTransaction> findByUserId(Long userId);

    List<StockTransaction> findByType(StockTransactionType type);

    Page<StockTransaction> findByType(StockTransactionType type, Pageable pageable);

    List<StockTransaction> findByReferenceTypeAndReferenceId(String referenceType, String referenceId);
}
