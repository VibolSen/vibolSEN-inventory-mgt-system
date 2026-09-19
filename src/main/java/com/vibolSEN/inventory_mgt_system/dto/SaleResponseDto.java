package com.vibolSEN.inventory_mgt_system.dto;

import com.vibolSEN.inventory_mgt_system.model.enums.DiscountType;
import com.vibolSEN.inventory_mgt_system.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponseDto {

    private Long id;
    private String saleNumber;
    private Long customerId;
    private String customerName;
    private Long userId;
    private String userName;
    private BigDecimal subtotal;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal changeAmount;
    private PaymentStatus paymentStatus;
    private String notes;
    @Builder.Default
    private List<SaleItemResponseDto> items = new ArrayList<>();
    @Builder.Default
    private List<SalePaymentResponseDto> payments = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
