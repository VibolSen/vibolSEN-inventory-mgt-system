package com.vibolSEN.inventory_mgt_system.dto;

import com.vibolSEN.inventory_mgt_system.model.enums.ShipmentItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentItemResponseDto {

    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Integer orderedQuantity;
    private Integer receivedQuantity;
    private Integer damagedQuantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private ShipmentItemStatus itemStatus;
    private String notes;
}
