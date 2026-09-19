package com.vibolSEN.inventory_mgt_system.service.impl;

import com.vibolSEN.inventory_mgt_system.dto.CartItemRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.CartItemResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.CartRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.CartResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.exception.ResourceNotFoundException;
import com.vibolSEN.inventory_mgt_system.model.Cart;
import com.vibolSEN.inventory_mgt_system.model.CartItem;
import com.vibolSEN.inventory_mgt_system.model.Customer;
import com.vibolSEN.inventory_mgt_system.model.Product;
import com.vibolSEN.inventory_mgt_system.model.User;
import com.vibolSEN.inventory_mgt_system.model.enums.CartStatus;
import com.vibolSEN.inventory_mgt_system.repository.CartItemRepository;
import com.vibolSEN.inventory_mgt_system.repository.CartRepository;
import com.vibolSEN.inventory_mgt_system.repository.CustomerRepository;
import com.vibolSEN.inventory_mgt_system.repository.ProductRepository;
import com.vibolSEN.inventory_mgt_system.repository.UserRepository;
import com.vibolSEN.inventory_mgt_system.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartResponseDto createCart(CartRequestDto requestDto) {
        Customer customer = null;
        if (requestDto.getCustomerId() != null) {
            customer = customerRepository.findById(requestDto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", requestDto.getCustomerId()));
        }

        User user = null;
        if (requestDto.getUserId() != null) {
            user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestDto.getUserId()));
        }

        String cartNumber = generateCartNumber();

        Cart cart = Cart.builder()
                .cartNumber(cartNumber)
                .status(requestDto.getStatus() != null ? requestDto.getStatus() : CartStatus.ACTIVE)
                .notes(requestDto.getNotes() != null ? requestDto.getNotes().trim() : null)
                .customer(customer)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        Cart savedCart = cartRepository.save(cart);

        if (requestDto.getItems() != null && !requestDto.getItems().isEmpty()) {
            for (CartItemRequestDto itemDto : requestDto.getItems()) {
                addItemToCartInternal(savedCart, itemDto);
            }
            savedCart = cartRepository.findById(savedCart.getId()).orElse(savedCart);
        }

        return mapToResponseDto(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartResponseDto> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CartResponseDto> getCartsPaginated(int page, int size, String sortBy, String sortDir, CartStatus status) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy != null ? sortBy : "id"));

        Page<Cart> cartPage = status != null 
                ? cartRepository.findByStatus(status, pageable) 
                : cartRepository.findAll(pageable);

        List<CartResponseDto> content = cartPage.getContent().stream()
                .map(this::mapToResponseDto)
                .toList();

        return PagedResponse.<CartResponseDto>builder()
                .content(content)
                .pageNumber(cartPage.getNumber())
                .pageSize(cartPage.getSize())
                .totalElements(cartPage.getTotalElements())
                .totalPages(cartPage.getTotalPages())
                .first(cartPage.isFirst())
                .last(cartPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDto getCartById(Long id) {
        return mapToResponseDto(findCartById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDto getCartByCartNumber(String cartNumber) {
        Cart cart = cartRepository.findByCartNumberIgnoreCase(cartNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartNumber", cartNumber));
        return mapToResponseDto(cart);
    }

    @Override
    @Transactional
    public CartResponseDto updateCart(Long id, CartRequestDto requestDto) {
        Cart cart = findCartById(id);

        if (requestDto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(requestDto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", requestDto.getCustomerId()));
            cart.setCustomer(customer);
        }

        if (requestDto.getUserId() != null) {
            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestDto.getUserId()));
            cart.setUser(user);
        }

        if (requestDto.getStatus() != null) {
            cart.setStatus(requestDto.getStatus());
        }

        if (requestDto.getNotes() != null) {
            cart.setNotes(requestDto.getNotes().trim());
        }

        Cart updated = cartRepository.save(cart);
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional
    public CartResponseDto addItemToCart(Long cartId, CartItemRequestDto itemDto) {
        Cart cart = findCartById(cartId);
        addItemToCartInternal(cart, itemDto);
        Cart refreshed = cartRepository.findById(cartId).orElse(cart);
        return mapToResponseDto(refreshed);
    }

    @Override
    @Transactional
    public CartResponseDto updateCartItem(Long cartId, Long itemId, CartItemRequestDto itemDto) {
        findCartById(cartId);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!cartItem.getCart().getId().equals(cartId)) {
            throw new ResourceNotFoundException("CartItem with id " + itemId + " does not belong to Cart " + cartId);
        }

        cartItem.setQuantity(itemDto.getQuantity());
        if (itemDto.getUnitPrice() != null) {
            cartItem.setUnitPrice(itemDto.getUnitPrice());
        }
        if (itemDto.getDiscountAmount() != null) {
            cartItem.setDiscountAmount(itemDto.getDiscountAmount());
        }
        if (itemDto.getNotes() != null) {
            cartItem.setNotes(itemDto.getNotes().trim());
        }

        cartItemRepository.save(cartItem);
        return mapToResponseDto(findCartById(cartId));
    }

    @Override
    @Transactional
    public CartResponseDto removeItemFromCart(Long cartId, Long itemId) {
        findCartById(cartId);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!cartItem.getCart().getId().equals(cartId)) {
            throw new ResourceNotFoundException("CartItem with id " + itemId + " does not belong to Cart " + cartId);
        }

        cartItemRepository.delete(cartItem);
        return mapToResponseDto(findCartById(cartId));
    }

    @Override
    @Transactional
    public CartResponseDto clearCart(Long cartId) {
        Cart cart = findCartById(cartId);
        cartItemRepository.deleteByCartId(cartId);
        cart.getCartItems().clear();
        return mapToResponseDto(cart);
    }

    @Override
    @Transactional
    public void deleteCart(Long id) {
        Cart cart = findCartById(id);
        cartItemRepository.deleteByCartId(id);
        cartRepository.delete(cart);
    }

    private void addItemToCartInternal(Cart cart, CartItemRequestDto itemDto) {
        Product product = productRepository.findById(itemDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDto.getProductId()));

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        BigDecimal unitPrice = itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : product.getUnitPrice();
        BigDecimal discount = itemDto.getDiscountAmount() != null ? itemDto.getDiscountAmount() : BigDecimal.ZERO;

        if (existingItemOpt.isPresent()) {
            CartItem existing = existingItemOpt.get();
            existing.setQuantity(existing.getQuantity() + itemDto.getQuantity());
            existing.setUnitPrice(unitPrice);
            existing.setDiscountAmount(discount);
            if (itemDto.getNotes() != null) {
                existing.setNotes(itemDto.getNotes().trim());
            }
            cartItemRepository.save(existing);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .unitPrice(unitPrice)
                    .discountAmount(discount)
                    .notes(itemDto.getNotes() != null ? itemDto.getNotes().trim() : null)
                    .build();
            cartItemRepository.save(newItem);
        }
    }

    private Cart findCartById(Long id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "id", id));
    }

    private String generateCartNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "CRT-" + timestamp + "-" + rand;
    }

    private CartResponseDto mapToResponseDto(Cart cart) {
        List<CartItemResponseDto> itemDtos = new ArrayList<>();
        int totalItems = 0;
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        if (cart.getCartItems() != null) {
            for (CartItem item : cart.getCartItems()) {
                BigDecimal itemSubtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                BigDecimal itemDiscount = item.getDiscountAmount() != null ? item.getDiscountAmount() : BigDecimal.ZERO;
                BigDecimal lineTotal = itemSubtotal.subtract(itemDiscount);

                totalItems += item.getQuantity();
                subtotal = subtotal.add(itemSubtotal);
                totalDiscount = totalDiscount.add(itemDiscount);

                itemDtos.add(CartItemResponseDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                        .productSku(item.getProduct() != null ? item.getProduct().getSku() : null)
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .discountAmount(itemDiscount)
                        .lineTotal(lineTotal)
                        .notes(item.getNotes())
                        .createdAt(item.getCreatedAt())
                        .updatedAt(item.getUpdatedAt())
                        .build());
            }
        }

        BigDecimal totalAmount = subtotal.subtract(totalDiscount);

        return CartResponseDto.builder()
                .id(cart.getId())
                .cartNumber(cart.getCartNumber())
                .status(cart.getStatus())
                .notes(cart.getNotes())
                .customerId(cart.getCustomer() != null ? cart.getCustomer().getId() : null)
                .customerName(cart.getCustomer() != null ? cart.getCustomer().getName() : null)
                .userId(cart.getUser() != null ? cart.getUser().getId() : null)
                .userName(cart.getUser() != null ? cart.getUser().getFullName() : null)
                .items(itemDtos)
                .totalItems(totalItems)
                .subtotal(subtotal)
                .totalDiscount(totalDiscount)
                .totalAmount(totalAmount)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
}
