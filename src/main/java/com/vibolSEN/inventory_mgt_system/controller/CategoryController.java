package com.vibolSEN.inventory_mgt_system.controller;

import com.vibolSEN.inventory_mgt_system.dto.ApiResponse;
import com.vibolSEN.inventory_mgt_system.dto.CategoryRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.CategoryResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.ProductResponseDto;
import com.vibolSEN.inventory_mgt_system.service.CategoryService;
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
@RequestMapping({"/api/v1/categories", "/api/v1/category", "/api/v1/categorys"})
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> createCategory(
            @Valid @RequestBody CategoryRequestDto requestDto) {
        CategoryResponseDto createdCategory = categoryService.createCategory(requestDto);
        return new ResponseEntity<>(
                ApiResponse.success("Category created successfully", createdCategory),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> getAllCategories() {
        List<CategoryResponseDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> getCategoryById(@PathVariable Long id) {
        CategoryResponseDto category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDto requestDto) {
        CategoryResponseDto updatedCategory = categoryService.updateCategory(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> searchCategories(
            @RequestParam(name = "name", required = false) String name) {
        List<CategoryResponseDto> categories = categoryService.searchCategoriesByName(name);
        return ResponseEntity.ok(ApiResponse.success("Categories search retrieved successfully", categories));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> getProductsByCategoryId(@PathVariable Long id) {
        List<ProductResponseDto> products = productService.getProductsByCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully for category", products));
    }
}
