package com.vibolSEN.inventory_mgt_system.controller;

import com.vibolSEN.inventory_mgt_system.dto.ApiResponse;
import com.vibolSEN.inventory_mgt_system.dto.CartItemRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.CartRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.CartResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.model.enums.CartStatus;
import com.vibolSEN.inventory_mgt_system.service.CartService;
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
@RequestMapping({"/api/v1/carts", "/api/v1/cart"})
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ApiResponse<CartResponseDto>> createCart(
            @Valid @RequestBody CartRequestDto requestDto) {
        CartResponseDto created = cartService.createCart(requestDto);
        return new ResponseEntity<>(
                ApiResponse.success("Cart created successfully", created),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCarts(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(name = "status", required = false) CartStatus status) {
        if (page != null) {
            PagedResponse<CartResponseDto> paged = cartService.getCartsPaginated(page, size, sortBy, sortDir, status);
            return ResponseEntity.ok(ApiResponse.success("Carts retrieved successfully (paginated)", paged));
        }
        List<CartResponseDto> carts = cartService.getAllCarts();
        return ResponseEntity.ok(ApiResponse.success("Carts retrieved successfully", carts));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<CartResponseDto>>> getCartsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) CartStatus status) {
        PagedResponse<CartResponseDto> paged = cartService.getCartsPaginated(page, size, sortBy, sortDir, status);
        return ResponseEntity.ok(ApiResponse.success("Carts retrieved successfully (paginated)", paged));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CartResponseDto>> getCartById(@PathVariable Long id) {
        CartResponseDto cart = cartService.getCartById(id);
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cart));
    }

    @GetMapping("/number/{cartNumber}")
    public ResponseEntity<ApiResponse<CartResponseDto>> getCartByCartNumber(@PathVariable String cartNumber) {
        CartResponseDto cart = cartService.getCartByCartNumber(cartNumber);
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cart));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CartResponseDto>> updateCart(
            @PathVariable Long id,
            @Valid @RequestBody CartRequestDto requestDto) {
        CartResponseDto updated = cartService.updateCart(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Cart updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.ok(ApiResponse.success("Cart deleted successfully"));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<ApiResponse<CartResponseDto>> addItemToCart(
            @PathVariable Long id,
            @Valid @RequestBody CartItemRequestDto itemDto) {
        CartResponseDto updated = cartService.addItemToCart(id, itemDto);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart successfully", updated));
    }

    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponseDto>> updateCartItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemRequestDto itemDto) {
        CartResponseDto updated = cartService.updateCartItem(id, itemId, itemDto);
        return ResponseEntity.ok(ApiResponse.success("Cart item updated successfully", updated));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponseDto>> removeItemFromCart(
            @PathVariable Long id,
            @PathVariable Long itemId) {
        CartResponseDto updated = cartService.removeItemFromCart(id, itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart successfully", updated));
    }

    @DeleteMapping("/{id}/clear")
    public ResponseEntity<ApiResponse<CartResponseDto>> clearCart(@PathVariable Long id) {
        CartResponseDto updated = cartService.clearCart(id);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully", updated));
    }
}
