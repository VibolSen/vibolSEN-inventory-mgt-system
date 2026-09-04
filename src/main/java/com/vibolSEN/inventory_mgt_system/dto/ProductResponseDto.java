package com.vibolSEN.inventory_mgt_system.dto;

import com.vibolSEN.inventory_mgt_system.model.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {

    private Long id;
    private String name;
    private String sku;
    private String barcode;
    private String description;
    private BigDecimal costPrice;
    private BigDecimal unitPrice;
    private Integer stockQuantity;
    private Integer minStockLevel;
    private String unitOfMeasure;
    private ProductStatus status;
    private CategoryResponseDto category;
    private Long supplierId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
