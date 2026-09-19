package com.vibolSEN.inventory_mgt_system.repository;

import com.vibolSEN.inventory_mgt_system.model.SalePayment;
import com.vibolSEN.inventory_mgt_system.model.enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalePaymentRepository extends JpaRepository<SalePayment, Long> {

    List<SalePayment> findBySaleId(Long saleId);

    List<SalePayment> findByPaymentMethod(PaymentMethod paymentMethod);
}
