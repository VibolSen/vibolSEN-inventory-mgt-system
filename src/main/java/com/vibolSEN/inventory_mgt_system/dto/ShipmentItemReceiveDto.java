package com.vibolSEN.inventory_mgt_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentItemReceiveDto {

    @NotNull(message = "Shipment item ID is required")
    private Long shipmentItemId;

    @NotNull(message = "Received quantity is required")
    @Min(value = 0, message = "Received quantity cannot be negative")
    private Integer receivedQuantity;

    @Min(value = 0, message = "Damaged quantity cannot be negative")
    @Builder.Default
    private Integer damagedQuantity = 0;

    @Size(max = 255, message = "Notes cannot exceed 255 characters")
    private String notes;
}
