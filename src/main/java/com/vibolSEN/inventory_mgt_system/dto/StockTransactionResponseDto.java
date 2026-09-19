package com.vibolSEN.inventory_mgt_system.dto;

import com.vibolSEN.inventory_mgt_system.model.enums.StockTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransactionResponseDto {

    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Long userId;
    private String userName;
    private StockTransactionType type;
    private Integer quantityChanged;
    private Integer balanceAfter;
    private String referenceType;
    private String referenceId;
    private String notes;
    private LocalDateTime createdAt;
}
