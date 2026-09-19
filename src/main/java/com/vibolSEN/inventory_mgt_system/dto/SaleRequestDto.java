package com.vibolSEN.inventory_mgt_system.dto;

import com.vibolSEN.inventory_mgt_system.model.enums.DiscountType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleRequestDto {

    private Long customerId;

    @NotNull(message = "User/Cashier ID is required")
    private Long userId;

    @Size(max = 100, message = "Customer name cannot exceed 100 characters")
    private String customerName;

    private Long cartId;

    @Builder.Default
    private DiscountType discountType = DiscountType.NONE;

    private BigDecimal discountValue;

    private BigDecimal taxAmount;

    private BigDecimal paidAmount;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @NotEmpty(message = "Sale must contain at least one item")
    @Valid
    private List<SaleItemRequestDto> items;

    @Valid
    private List<SalePaymentRequestDto> payments;
}
