package com.vibolSEN.inventory_mgt_system.controller;

import com.vibolSEN.inventory_mgt_system.dto.ApiResponse;
import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.ShipmentReceiveRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.ShipmentRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.ShipmentResponseDto;
import com.vibolSEN.inventory_mgt_system.model.enums.ShipmentStatus;
import com.vibolSEN.inventory_mgt_system.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/shipments", "/api/v1/shipment"})
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<ShipmentResponseDto>> createShipment(
            @Valid @RequestBody ShipmentRequestDto requestDto) {
        ShipmentResponseDto created = shipmentService.createShipment(requestDto);
        return new ResponseEntity<>(
                ApiResponse.success("Shipment created successfully", created),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllShipments(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(name = "status", required = false) ShipmentStatus status) {
        if (page != null) {
            PagedResponse<ShipmentResponseDto> paged = shipmentService.getShipmentsPaginated(page, size, sortBy, sortDir, status);
            return ResponseEntity.ok(ApiResponse.success("Shipments retrieved successfully (paginated)", paged));
        }
        List<ShipmentResponseDto> shipments = shipmentService.getAllShipments();
        return ResponseEntity.ok(ApiResponse.success("Shipments retrieved successfully", shipments));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<ShipmentResponseDto>>> getShipmentsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) ShipmentStatus status) {
        PagedResponse<ShipmentResponseDto> paged = shipmentService.getShipmentsPaginated(page, size, sortBy, sortDir, status);
        return ResponseEntity.ok(ApiResponse.success("Shipments retrieved successfully (paginated)", paged));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentResponseDto>> getShipmentById(@PathVariable Long id) {
        ShipmentResponseDto shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Shipment retrieved successfully", shipment));
    }

    @GetMapping("/number/{shipmentNumber}")
    public ResponseEntity<ApiResponse<ShipmentResponseDto>> getShipmentByNumber(@PathVariable String shipmentNumber) {
        ShipmentResponseDto shipment = shipmentService.getShipmentByNumber(shipmentNumber);
        return ResponseEntity.ok(ApiResponse.success("Shipment retrieved successfully", shipment));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<ApiResponse<List<ShipmentResponseDto>>> getShipmentsBySupplier(@PathVariable Long supplierId) {
        List<ShipmentResponseDto> shipments = shipmentService.getShipmentsBySupplier(supplierId);
        return ResponseEntity.ok(ApiResponse.success("Supplier shipments retrieved successfully", shipments));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentResponseDto>> updateShipment(
            @PathVariable Long id,
            @Valid @RequestBody ShipmentRequestDto requestDto) {
        ShipmentResponseDto updated = shipmentService.updateShipment(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Shipment updated successfully", updated));
    }

    @PostMapping("/{id}/receive")
    public ResponseEntity<ApiResponse<ShipmentResponseDto>> receiveShipment(
            @PathVariable Long id,
            @Valid @RequestBody ShipmentReceiveRequestDto receiveRequestDto) {
        ShipmentResponseDto updated = shipmentService.receiveShipment(id, receiveRequestDto);
        return ResponseEntity.ok(ApiResponse.success("Shipment received and inventory updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShipment(@PathVariable Long id) {
        shipmentService.deleteShipment(id);
        return ResponseEntity.ok(ApiResponse.success("Shipment deleted successfully"));
    }
}
