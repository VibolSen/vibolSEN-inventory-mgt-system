package com.vibolSEN.inventory_mgt_system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentReceiveRequestDto {

    @NotEmpty(message = "Items to receive cannot be empty")
    @Valid
    private List<ShipmentItemReceiveDto> items;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
