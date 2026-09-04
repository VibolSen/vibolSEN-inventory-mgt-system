package com.vibolSEN.inventory_mgt_system.service.impl;

import com.vibolSEN.inventory_mgt_system.dto.CategoryResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.ProductRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.ProductResponseDto;
import com.vibolSEN.inventory_mgt_system.exception.DuplicateResourceException;
import com.vibolSEN.inventory_mgt_system.exception.ResourceNotFoundException;
import com.vibolSEN.inventory_mgt_system.model.Category;
import com.vibolSEN.inventory_mgt_system.model.Product;
import com.vibolSEN.inventory_mgt_system.model.enums.ProductStatus;
import com.vibolSEN.inventory_mgt_system.repository.CategoryRepository;
import com.vibolSEN.inventory_mgt_system.repository.ProductRepository;
import com.vibolSEN.inventory_mgt_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        String trimmedSku = requestDto.getSku().trim();
        if (productRepository.existsBySkuIgnoreCase(trimmedSku)) {
            throw new DuplicateResourceException("Product", "SKU", trimmedSku);
        }

        String trimmedBarcode = requestDto.getBarcode() != null ? requestDto.getBarcode().trim() : null;
        if (trimmedBarcode != null && !trimmedBarcode.isEmpty() && productRepository.existsByBarcode(trimmedBarcode)) {
            throw new DuplicateResourceException("Product", "barcode", trimmedBarcode);
        }

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", requestDto.getCategoryId()));

        Product product = Product.builder()
                .name(requestDto.getName().trim())
                .sku(trimmedSku)
                .barcode(trimmedBarcode != null && !trimmedBarcode.isEmpty() ? trimmedBarcode : null)
                .description(requestDto.getDescription() != null ? requestDto.getDescription().trim() : null)
                .costPrice(requestDto.getCostPrice())
                .unitPrice(requestDto.getUnitPrice())
                .stockQuantity(requestDto.getStockQuantity())
                .minStockLevel(requestDto.getMinStockLevel())
                .unitOfMeasure(requestDto.getUnitOfMeasure() != null ? requestDto.getUnitOfMeasure().trim() : null)
                .status(requestDto.getStatus() != null ? requestDto.getStatus() : ProductStatus.ACTIVE)
                .category(category)
                .supplierId(requestDto.getSupplierId())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponseDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponseDto> getProductsPaginated(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy != null ? sortBy : "id"));

        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductResponseDto> content = productPage.getContent().stream()
                .map(this::mapToResponseDto)
                .toList();

        return PagedResponse.<ProductResponseDto>builder()
                .content(content)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = findProductById(id);
        return mapToResponseDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        String trimmed = keyword.trim();
        return productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(trimmed, trimmed).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        Product product = findProductById(id);

        String trimmedSku = requestDto.getSku().trim();
        if (productRepository.existsBySkuIgnoreCaseAndIdNot(trimmedSku, id)) {
            throw new DuplicateResourceException("Product", "SKU", trimmedSku);
        }

        String trimmedBarcode = requestDto.getBarcode() != null ? requestDto.getBarcode().trim() : null;
        if (trimmedBarcode != null && !trimmedBarcode.isEmpty() && productRepository.existsByBarcodeAndIdNot(trimmedBarcode, id)) {
            throw new DuplicateResourceException("Product", "barcode", trimmedBarcode);
        }

        if (!product.getCategory().getId().equals(requestDto.getCategoryId())) {
            Category category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", requestDto.getCategoryId()));
            product.setCategory(category);
        }

        product.setName(requestDto.getName().trim());
        product.setSku(trimmedSku);
        product.setBarcode(trimmedBarcode != null && !trimmedBarcode.isEmpty() ? trimmedBarcode : null);
        product.setDescription(requestDto.getDescription() != null ? requestDto.getDescription().trim() : null);
        product.setCostPrice(requestDto.getCostPrice());
        product.setUnitPrice(requestDto.getUnitPrice());
        product.setStockQuantity(requestDto.getStockQuantity());
        product.setMinStockLevel(requestDto.getMinStockLevel());
        product.setUnitOfMeasure(requestDto.getUnitOfMeasure() != null ? requestDto.getUnitOfMeasure().trim() : null);
        if (requestDto.getStatus() != null) {
            product.setStatus(requestDto.getStatus());
        }
        product.setSupplierId(requestDto.getSupplierId());

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = findProductById(id);
        productRepository.delete(product);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }

    private ProductResponseDto mapToResponseDto(Product product) {
        CategoryResponseDto categoryDto = null;
        if (product.getCategory() != null) {
            categoryDto = CategoryResponseDto.builder()
                    .id(product.getCategory().getId())
                    .name(product.getCategory().getName())
                    .description(product.getCategory().getDescription())
                    .createdAt(product.getCategory().getCreatedAt())
                    .updatedAt(product.getCategory().getUpdatedAt())
                    .build();
        }

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .description(product.getDescription())
                .costPrice(product.getCostPrice())
                .unitPrice(product.getUnitPrice())
                .stockQuantity(product.getStockQuantity())
                .minStockLevel(product.getMinStockLevel())
                .unitOfMeasure(product.getUnitOfMeasure())
                .status(product.getStatus())
                .category(categoryDto)
                .supplierId(product.getSupplierId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
