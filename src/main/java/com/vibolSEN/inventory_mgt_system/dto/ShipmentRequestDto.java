package com.vibolSEN.inventory_mgt_system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentRequestDto {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @Size(max = 100, message = "Tracking number cannot exceed 100 characters")
    private String trackingNumber;

    private LocalDate expectedDate;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @NotEmpty(message = "Shipment must contain at least one item")
    @Valid
    private List<ShipmentItemRequestDto> items;
}
