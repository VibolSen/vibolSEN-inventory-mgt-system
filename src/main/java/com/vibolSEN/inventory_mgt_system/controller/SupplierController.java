package com.vibolSEN.inventory_mgt_system.controller;

import com.vibolSEN.inventory_mgt_system.dto.ApiResponse;
import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.SupplierRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.SupplierResponseDto;
import com.vibolSEN.inventory_mgt_system.service.SupplierService;
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
@RequestMapping({"/api/v1/suppliers", "/api/v1/supplier"})
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierResponseDto>> createSupplier(
            @Valid @RequestBody SupplierRequestDto requestDto) {
        SupplierResponseDto created = supplierService.createSupplier(requestDto);
        return new ResponseEntity<>(
                ApiResponse.success("Supplier created successfully", created),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllSuppliers(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir) {
        if (page != null) {
            PagedResponse<SupplierResponseDto> paged = supplierService.getSuppliersPaginated(page, size, sortBy, sortDir);
            return ResponseEntity.ok(ApiResponse.success("Suppliers retrieved successfully (paginated)", paged));
        }
        List<SupplierResponseDto> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(ApiResponse.success("Suppliers retrieved successfully", suppliers));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<SupplierResponseDto>>> getSuppliersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PagedResponse<SupplierResponseDto> paged = supplierService.getSuppliersPaginated(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Suppliers retrieved successfully (paginated)", paged));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponseDto>> getSupplierById(@PathVariable Long id) {
        SupplierResponseDto supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier retrieved successfully", supplier));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponseDto>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequestDto requestDto) {
        SupplierResponseDto updated = supplierService.updateSupplier(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Supplier updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SupplierResponseDto>>> searchSuppliers(
            @RequestParam(name = "keyword", required = false) String keyword) {
        List<SupplierResponseDto> results = supplierService.searchSuppliers(keyword);
        return ResponseEntity.ok(ApiResponse.success("Suppliers search retrieved successfully", results));
    }
}
