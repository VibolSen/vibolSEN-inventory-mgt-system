package com.vibolSEN.inventory_mgt_system.service;

import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.ProductRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.ProductResponseDto;

import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto requestDto);

    List<ProductResponseDto> getAllProducts();

    PagedResponse<ProductResponseDto> getProductsPaginated(int page, int size, String sortBy, String sortDir);

    ProductResponseDto getProductById(Long id);

    List<ProductResponseDto> getProductsByCategory(Long categoryId);

    List<ProductResponseDto> searchProducts(String keyword);

    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);

    void deleteProduct(Long id);
}
