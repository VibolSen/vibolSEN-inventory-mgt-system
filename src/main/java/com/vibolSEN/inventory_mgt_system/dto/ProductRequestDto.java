package com.vibolSEN.inventory_mgt_system.dto;

import com.vibolSEN.inventory_mgt_system.model.enums.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ProductRequestDto {

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;

    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    private String sku;

    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    private String barcode;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Cost price is required")
    @DecimalMin(value = "0.00", message = "Cost price must be greater than or equal to 0")
    private BigDecimal costPrice;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.00", message = "Unit price must be greater than or equal to 0")
    private BigDecimal unitPrice;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
    private Integer stockQuantity;

    @NotNull(message = "Minimum stock level is required")
    @Min(value = 0, message = "Minimum stock level must be greater than or equal to 0")
    private Integer minStockLevel;

    @Size(max = 50, message = "Unit of measure must not exceed 50 characters")
    private String unitOfMeasure;

    private ProductStatus status;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private Long supplierId;
}
