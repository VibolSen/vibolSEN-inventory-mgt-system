package com.vibolSEN.inventory_mgt_system.controller;

import com.vibolSEN.inventory_mgt_system.dto.ApiResponse;
import com.vibolSEN.inventory_mgt_system.dto.CustomerRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.CustomerResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/customers", "/api/v1/customer"})
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponseDto>> createCustomer(
            @Valid @RequestBody CustomerRequestDto requestDto) {
        CustomerResponseDto created = customerService.createCustomer(requestDto);
        return new ResponseEntity<>(
                ApiResponse.success("Customer created successfully", created),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCustomers(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir) {
        if (page != null) {
            PagedResponse<CustomerResponseDto> paged = customerService.getCustomersPaginated(page, size, sortBy, sortDir);
            return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully (paginated)", paged));
        }
        List<CustomerResponseDto> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", customers));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<CustomerResponseDto>>> getCustomersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PagedResponse<CustomerResponseDto> paged = customerService.getCustomersPaginated(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully (paginated)", paged));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> getCustomerById(@PathVariable Long id) {
        CustomerResponseDto customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success("Customer retrieved successfully", customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto requestDto) {
        CustomerResponseDto updated = customerService.updateCustomer(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CustomerResponseDto>>> searchCustomers(
            @RequestParam(name = "keyword", required = false) String keyword) {
        List<CustomerResponseDto> results = customerService.searchCustomers(keyword);
        return ResponseEntity.ok(ApiResponse.success("Customers search retrieved successfully", results));
    }

    @PatchMapping("/{id}/loyalty-points")
    public ResponseEntity<ApiResponse<CustomerResponseDto>> adjustLoyaltyPoints(
            @PathVariable Long id,
            @RequestParam(name = "points") int points) {
        CustomerResponseDto updated = customerService.adjustLoyaltyPoints(id, points);
        return ResponseEntity.ok(ApiResponse.success("Loyalty points updated successfully", updated));
    }
}
