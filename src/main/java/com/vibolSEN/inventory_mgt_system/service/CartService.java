package com.vibolSEN.inventory_mgt_system.service;

import com.vibolSEN.inventory_mgt_system.dto.CartItemRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.CartRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.CartResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.model.enums.CartStatus;

import java.util.List;

public interface CartService {

    CartResponseDto createCart(CartRequestDto requestDto);

    List<CartResponseDto> getAllCarts();

    PagedResponse<CartResponseDto> getCartsPaginated(int page, int size, String sortBy, String sortDir, CartStatus status);

    CartResponseDto getCartById(Long id);

    CartResponseDto getCartByCartNumber(String cartNumber);

    CartResponseDto updateCart(Long id, CartRequestDto requestDto);

    CartResponseDto addItemToCart(Long cartId, CartItemRequestDto itemDto);

    CartResponseDto updateCartItem(Long cartId, Long itemId, CartItemRequestDto itemDto);

    CartResponseDto removeItemFromCart(Long cartId, Long itemId);

    CartResponseDto clearCart(Long cartId);

    void deleteCart(Long id);
}
