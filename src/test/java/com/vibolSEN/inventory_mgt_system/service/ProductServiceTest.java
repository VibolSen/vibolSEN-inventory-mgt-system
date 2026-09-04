package com.vibolSEN.inventory_mgt_system.service;

import com.vibolSEN.inventory_mgt_system.dto.ProductRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.ProductResponseDto;
import com.vibolSEN.inventory_mgt_system.exception.DuplicateResourceException;
import com.vibolSEN.inventory_mgt_system.exception.ResourceNotFoundException;
import com.vibolSEN.inventory_mgt_system.model.Category;
import com.vibolSEN.inventory_mgt_system.model.Product;
import com.vibolSEN.inventory_mgt_system.model.enums.ProductStatus;
import com.vibolSEN.inventory_mgt_system.repository.CategoryRepository;
import com.vibolSEN.inventory_mgt_system.repository.ProductRepository;
import com.vibolSEN.inventory_mgt_system.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;
    private ProductRequestDto requestDto;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic gadgets")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        product = Product.builder()
                .id(1L)
                .name("Wireless Mouse")
                .sku("SKU-WM-001")
                .barcode("1234567890123")
                .description("Ergonomic wireless mouse")
                .costPrice(new BigDecimal("15.00"))
                .unitPrice(new BigDecimal("29.99"))
                .stockQuantity(100)
                .minStockLevel(10)
                .unitOfMeasure("PCS")
                .status(ProductStatus.ACTIVE)
                .category(category)
                .supplierId(10L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestDto = ProductRequestDto.builder()
                .name("Wireless Mouse")
                .sku("SKU-WM-001")
                .barcode("1234567890123")
                .description("Ergonomic wireless mouse")
                .costPrice(new BigDecimal("15.00"))
                .unitPrice(new BigDecimal("29.99"))
                .stockQuantity(100)
                .minStockLevel(10)
                .unitOfMeasure("PCS")
                .status(ProductStatus.ACTIVE)
                .categoryId(1L)
                .supplierId(10L)
                .build();
    }

    @Test
    @DisplayName("Create Product - Success")
    void createProduct_Success() {
        when(productRepository.existsBySkuIgnoreCase("SKU-WM-001")).thenReturn(false);
        when(productRepository.existsByBarcode("1234567890123")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDto response = productService.createProduct(requestDto);

        assertNotNull(response);
        assertEquals("Wireless Mouse", response.getName());
        assertEquals("SKU-WM-001", response.getSku());
        assertEquals("Electronics", response.getCategory().getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Create Product - Duplicate SKU Throws DuplicateResourceException")
    void createProduct_DuplicateSku_ThrowsException() {
        when(productRepository.existsBySkuIgnoreCase("SKU-WM-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> productService.createProduct(requestDto));
    }

    @Test
    @DisplayName("Create Product - Category Not Found Throws ResourceNotFoundException")
    void createProduct_CategoryNotFound_ThrowsException() {
        when(productRepository.existsBySkuIgnoreCase("SKU-WM-001")).thenReturn(false);
        when(productRepository.existsByBarcode("1234567890123")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(requestDto));
    }

    @Test
    @DisplayName("Get Product By ID - Success")
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDto response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Wireless Mouse", response.getName());
    }

    @Test
    @DisplayName("Get Product By ID - Not Found Throws ResourceNotFoundException")
    void getProductById_NotFound_ThrowsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    @DisplayName("Get Products By Category - Success")
    void getProductsByCategory_Success() {
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findByCategoryId(1L)).thenReturn(List.of(product));

        List<ProductResponseDto> list = productService.getProductsByCategory(1L);

        assertEquals(1, list.size());
        assertEquals("Wireless Mouse", list.get(0).getName());
    }

    @Test
    @DisplayName("Get Products Paginated - Success")
    void getProductsPaginated_Success() {
        org.springframework.data.domain.Page<Product> page = new org.springframework.data.domain.PageImpl<>(List.of(product));
        when(productRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

        com.vibolSEN.inventory_mgt_system.dto.PagedResponse<ProductResponseDto> pagedResponse =
                productService.getProductsPaginated(0, 10, "id", "asc");

        assertNotNull(pagedResponse);
        assertEquals(1, pagedResponse.getContent().size());
        assertEquals(1, pagedResponse.getTotalElements());
        assertEquals(1, pagedResponse.getTotalPages());
    }

    @Test
    @DisplayName("Delete Product - Success")
    void deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }
}
