package com.vibolSEN.inventory_mgt_system.controller;

import com.vibolSEN.inventory_mgt_system.dto.ApiResponse;
import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.StockAdjustmentRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.StockTransactionResponseDto;
import com.vibolSEN.inventory_mgt_system.model.enums.StockTransactionType;
import com.vibolSEN.inventory_mgt_system.service.StockTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/stock-transactions", "/api/v1/stock-transaction"})
@RequiredArgsConstructor
public class StockTransactionController {

    private final StockTransactionService stockTransactionService;

    @PostMapping("/adjust")
    public ResponseEntity<ApiResponse<StockTransactionResponseDto>> adjustStock(
            @Valid @RequestBody StockAdjustmentRequestDto requestDto) {
        StockTransactionResponseDto created = stockTransactionService.adjustStock(requestDto);
        return new ResponseEntity<>(
                ApiResponse.success("Stock adjustment completed successfully", created),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllTransactions(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(name = "type", required = false) StockTransactionType type) {
        if (page != null) {
            PagedResponse<StockTransactionResponseDto> paged = stockTransactionService.getTransactionsPaginated(page, size, sortBy, sortDir, type);
            return ResponseEntity.ok(ApiResponse.success("Stock transactions retrieved successfully (paginated)", paged));
        }
        List<StockTransactionResponseDto> list = stockTransactionService.getAllTransactions();
        return ResponseEntity.ok(ApiResponse.success("Stock transactions retrieved successfully", list));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<StockTransactionResponseDto>>> getTransactionsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) StockTransactionType type) {
        PagedResponse<StockTransactionResponseDto> paged = stockTransactionService.getTransactionsPaginated(page, size, sortBy, sortDir, type);
        return ResponseEntity.ok(ApiResponse.success("Stock transactions retrieved successfully (paginated)", paged));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StockTransactionResponseDto>> getTransactionById(@PathVariable Long id) {
        StockTransactionResponseDto tx = stockTransactionService.getTransactionById(id);
        return ResponseEntity.ok(ApiResponse.success("Stock transaction retrieved successfully", tx));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<StockTransactionResponseDto>>> getTransactionsByProduct(@PathVariable Long productId) {
        List<StockTransactionResponseDto> list = stockTransactionService.getTransactionsByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success("Product stock transactions retrieved successfully", list));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<StockTransactionResponseDto>>> getTransactionsByUser(@PathVariable Long userId) {
        List<StockTransactionResponseDto> list = stockTransactionService.getTransactionsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User stock transactions retrieved successfully", list));
    }
}
