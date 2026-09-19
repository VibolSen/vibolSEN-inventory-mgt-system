package com.vibolSEN.inventory_mgt_system.service;

import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.ShipmentReceiveRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.ShipmentRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.ShipmentResponseDto;
import com.vibolSEN.inventory_mgt_system.model.enums.ShipmentStatus;

import java.util.List;

public interface ShipmentService {

    ShipmentResponseDto createShipment(ShipmentRequestDto requestDto);

    List<ShipmentResponseDto> getAllShipments();

    PagedResponse<ShipmentResponseDto> getShipmentsPaginated(int page, int size, String sortBy, String sortDir, ShipmentStatus status);

    ShipmentResponseDto getShipmentById(Long id);

    ShipmentResponseDto getShipmentByNumber(String shipmentNumber);

    List<ShipmentResponseDto> getShipmentsBySupplier(Long supplierId);

    ShipmentResponseDto updateShipment(Long id, ShipmentRequestDto requestDto);

    ShipmentResponseDto receiveShipment(Long id, ShipmentReceiveRequestDto receiveRequestDto);

    void deleteShipment(Long id);
}
