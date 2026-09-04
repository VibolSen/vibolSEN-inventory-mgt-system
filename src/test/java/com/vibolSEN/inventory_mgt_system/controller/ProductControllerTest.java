package com.vibolSEN.inventory_mgt_system.controller;

import com.vibolSEN.inventory_mgt_system.dto.CategoryResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.ProductRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.ProductResponseDto;
import com.vibolSEN.inventory_mgt_system.exception.ResourceNotFoundException;
import com.vibolSEN.inventory_mgt_system.model.enums.ProductStatus;
import com.vibolSEN.inventory_mgt_system.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private ProductResponseDto responseDto;

    private static final String VALID_PRODUCT_JSON = """
            {
                "name": "Wireless Mouse",
                "sku": "SKU-WM-001",
                "barcode": "1234567890123",
                "description": "Ergonomic wireless mouse",
                "costPrice": 15.00,
                "unitPrice": 29.99,
                "stockQuantity": 100,
                "minStockLevel": 10,
                "unitOfMeasure": "PCS",
                "status": "ACTIVE",
                "categoryId": 1,
                "supplierId": 10
            }
            """;

    @BeforeEach
    void setUp() {
        CategoryResponseDto categoryDto = CategoryResponseDto.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic gadgets")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        responseDto = ProductResponseDto.builder()
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
                .category(categoryDto)
                .supplierId(10L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/products - Success returns 201")
    void createProduct_Success() throws Exception {
        when(productService.createProduct(any(ProductRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Wireless Mouse"))
                .andExpect(jsonPath("$.data.sku").value("SKU-WM-001"))
                .andExpect(jsonPath("$.data.category.id").value(1))
                .andExpect(jsonPath("$.data.category.name").value("Electronics"));
    }

    @Test
    @DisplayName("POST /api/v1/products - Validation error returns 400")
    void createProduct_ValidationError() throws Exception {
        String invalidJson = """
                {
                    "name": "",
                    "sku": ""
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.sku").exists());
    }

    @Test
    @DisplayName("GET /api/v1/products - Success returns 200")
    void getAllProducts_Success() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Wireless Mouse"));
    }

    @Test
    @DisplayName("GET /api/v1/products/paged - Success returns 200")
    void getProductsPaginated_Success() throws Exception {
        com.vibolSEN.inventory_mgt_system.dto.PagedResponse<ProductResponseDto> pagedResponse =
                com.vibolSEN.inventory_mgt_system.dto.PagedResponse.<ProductResponseDto>builder()
                        .content(List.of(responseDto))
                        .pageNumber(0)
                        .pageSize(10)
                        .totalElements(1)
                        .totalPages(1)
                        .first(true)
                        .last(true)
                        .build();

        when(productService.getProductsPaginated(0, 10, "id", "asc")).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/products/paged?page=0&size=10&sortBy=id&sortDir=asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Success returns 200")
    void getProductById_Success() throws Exception {
        when(productService.getProductById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Not found returns 404")
    void getProductById_NotFound() throws Exception {
        when(productService.getProductById(99L)).thenThrow(new ResourceNotFoundException("Product", "id", 99L));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Product not found with id: '99'"));
    }

    @Test
    @DisplayName("GET /api/v1/products/category/{categoryId} - Success returns 200")
    void getProductsByCategory_Success() throws Exception {
        when(productService.getProductsByCategory(1L)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/products/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].category.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id} - Success returns 200")
    void updateProduct_Success() throws Exception {
        when(productService.updateProduct(eq(1L), any(ProductRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_PRODUCT_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} - Success returns 200")
    void deleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));
    }
}
