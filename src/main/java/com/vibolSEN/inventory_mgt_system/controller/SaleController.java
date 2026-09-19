package com.vibolSEN.inventory_mgt_system.controller;

import com.vibolSEN.inventory_mgt_system.dto.ApiResponse;
import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.SalePaymentRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.SaleRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.SaleResponseDto;
import com.vibolSEN.inventory_mgt_system.model.enums.PaymentStatus;
import com.vibolSEN.inventory_mgt_system.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/sales", "/api/v1/sale"})
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<ApiResponse<SaleResponseDto>> createSale(
            @Valid @RequestBody SaleRequestDto requestDto) {
        SaleResponseDto created = saleService.createSale(requestDto);
        return new ResponseEntity<>(
                ApiResponse.success("Sale completed successfully", created),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllSales(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(name = "paymentStatus", required = false) PaymentStatus paymentStatus) {
        if (page != null) {
            PagedResponse<SaleResponseDto> paged = saleService.getSalesPaginated(page, size, sortBy, sortDir, paymentStatus);
            return ResponseEntity.ok(ApiResponse.success("Sales retrieved successfully (paginated)", paged));
        }
        List<SaleResponseDto> sales = saleService.getAllSales();
        return ResponseEntity.ok(ApiResponse.success("Sales retrieved successfully", sales));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<SaleResponseDto>>> getSalesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) PaymentStatus paymentStatus) {
        PagedResponse<SaleResponseDto> paged = saleService.getSalesPaginated(page, size, sortBy, sortDir, paymentStatus);
        return ResponseEntity.ok(ApiResponse.success("Sales retrieved successfully (paginated)", paged));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SaleResponseDto>> getSaleById(@PathVariable Long id) {
        SaleResponseDto sale = saleService.getSaleById(id);
        return ResponseEntity.ok(ApiResponse.success("Sale retrieved successfully", sale));
    }

    @GetMapping("/number/{saleNumber}")
    public ResponseEntity<ApiResponse<SaleResponseDto>> getSaleByNumber(@PathVariable String saleNumber) {
        SaleResponseDto sale = saleService.getSaleBySaleNumber(saleNumber);
        return ResponseEntity.ok(ApiResponse.success("Sale retrieved successfully", sale));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<SaleResponseDto>>> getSalesByCustomerId(@PathVariable Long customerId) {
        List<SaleResponseDto> sales = saleService.getSalesByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success("Customer sales retrieved successfully", sales));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SaleResponseDto>>> searchSales(
            @RequestParam(name = "keyword", required = false) String keyword) {
        List<SaleResponseDto> results = saleService.searchSales(keyword);
        return ResponseEntity.ok(ApiResponse.success("Sales search retrieved successfully", results));
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<ApiResponse<SaleResponseDto>> addPaymentToSale(
            @PathVariable Long id,
            @Valid @RequestBody SalePaymentRequestDto paymentRequestDto) {
        SaleResponseDto updated = saleService.addPaymentToSale(id, paymentRequestDto);
        return ResponseEntity.ok(ApiResponse.success("Payment added to sale successfully", updated));
    }

    @PatchMapping("/{id}/notes")
    public ResponseEntity<ApiResponse<SaleResponseDto>> updateSaleNotes(
            @PathVariable Long id,
            @RequestParam(name = "notes") String notes) {
        SaleResponseDto updated = saleService.updateSaleNotes(id, notes);
        return ResponseEntity.ok(ApiResponse.success("Sale notes updated successfully", updated));
    }
}
