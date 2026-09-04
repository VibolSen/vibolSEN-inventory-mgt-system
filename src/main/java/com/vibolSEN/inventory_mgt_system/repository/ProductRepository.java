package com.vibolSEN.inventory_mgt_system.repository;

import com.vibolSEN.inventory_mgt_system.model.Product;
import com.vibolSEN.inventory_mgt_system.model.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

    Optional<Product> findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);

    boolean existsByBarcodeAndIdNot(String barcode, Long id);

    List<Product> findByCategoryId(Long categoryId);

    boolean existsByCategoryId(Long categoryId);

    List<Product> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(String name, String sku);

    List<Product> findByStatus(ProductStatus status);
}
