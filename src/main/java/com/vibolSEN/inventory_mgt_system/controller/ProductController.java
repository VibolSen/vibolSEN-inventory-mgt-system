package com.vibolSEN.inventory_mgt_system.controller;

import com.vibolSEN.inventory_mgt_system.dto.ApiResponse;
import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.ProductRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.ProductResponseDto;
import com.vibolSEN.inventory_mgt_system.service.ProductService;
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
@RequestMapping({"/api/v1/products", "/api/v1/product"})
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(
            @Valid @RequestBody ProductRequestDto requestDto) {
        ProductResponseDto createdProduct = productService.createProduct(requestDto);
        return new ResponseEntity<>(
                ApiResponse.success("Product created successfully", createdProduct),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getAllProducts() {
        List<ProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", products));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<ProductResponseDto>>> getProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PagedResponse<ProductResponseDto> pagedProducts = productService.getProductsPaginated(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully (paginated)", pagedProducts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductById(@PathVariable Long id) {
        ProductResponseDto product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", product));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductResponseDto> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully for category", products));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> searchProducts(
            @RequestParam(name = "keyword", required = false) String keyword) {
        List<ProductResponseDto> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(ApiResponse.success("Products search retrieved successfully", products));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto requestDto) {
        ProductResponseDto updatedProduct = productService.updateProduct(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }
}
