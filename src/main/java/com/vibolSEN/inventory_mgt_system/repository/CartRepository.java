package com.vibolSEN.inventory_mgt_system.repository;

import com.vibolSEN.inventory_mgt_system.model.Cart;
import com.vibolSEN.inventory_mgt_system.model.enums.CartStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCartNumberIgnoreCase(String cartNumber);

    boolean existsByCartNumberIgnoreCase(String cartNumber);

    List<Cart> findByStatus(CartStatus status);

    Page<Cart> findByStatus(CartStatus status, Pageable pageable);

    List<Cart> findByCustomerId(Long customerId);

    List<Cart> findByUserId(Long userId);

    List<Cart> findByStatusAndUserId(CartStatus status, Long userId);
}
