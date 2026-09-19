package com.vibolSEN.inventory_mgt_system.dto;

import com.vibolSEN.inventory_mgt_system.model.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentResponseDto {

    private Long id;
    private String shipmentNumber;
    private String trackingNumber;
    private Long supplierId;
    private String supplierName;
    private Long userId;
    private String userName;
    private ShipmentStatus status;
    private LocalDate expectedDate;
    private LocalDateTime receivedDate;
    private BigDecimal totalCost;
    private String notes;
    @Builder.Default
    private List<ShipmentItemResponseDto> items = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
