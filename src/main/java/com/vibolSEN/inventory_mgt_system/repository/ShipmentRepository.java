package com.vibolSEN.inventory_mgt_system.repository;

import com.vibolSEN.inventory_mgt_system.model.Shipment;
import com.vibolSEN.inventory_mgt_system.model.enums.ShipmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByShipmentNumberIgnoreCase(String shipmentNumber);

    boolean existsByShipmentNumberIgnoreCase(String shipmentNumber);

    List<Shipment> findBySupplierId(Long supplierId);

    List<Shipment> findByUserId(Long userId);

    List<Shipment> findByStatus(ShipmentStatus status);

    Page<Shipment> findByStatus(ShipmentStatus status, Pageable pageable);
}
