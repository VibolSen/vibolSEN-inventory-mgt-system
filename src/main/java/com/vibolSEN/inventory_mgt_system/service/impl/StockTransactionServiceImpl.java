package com.vibolSEN.inventory_mgt_system.service.impl;

import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.StockAdjustmentRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.StockTransactionResponseDto;
import com.vibolSEN.inventory_mgt_system.exception.ResourceNotFoundException;
import com.vibolSEN.inventory_mgt_system.model.Product;
import com.vibolSEN.inventory_mgt_system.model.StockTransaction;
import com.vibolSEN.inventory_mgt_system.model.User;
import com.vibolSEN.inventory_mgt_system.model.enums.ProductStatus;
import com.vibolSEN.inventory_mgt_system.model.enums.StockTransactionType;
import com.vibolSEN.inventory_mgt_system.repository.ProductRepository;
import com.vibolSEN.inventory_mgt_system.repository.StockTransactionRepository;
import com.vibolSEN.inventory_mgt_system.repository.UserRepository;
import com.vibolSEN.inventory_mgt_system.service.StockTransactionService;
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
public class StockTransactionServiceImpl implements StockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StockTransactionResponseDto adjustStock(StockAdjustmentRequestDto requestDto) {
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", requestDto.getProductId()));

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestDto.getUserId()));

        int currentStock = product.getStockQuantity();
        int delta;
        int newBalance;

        StockTransactionType type = requestDto.getType();
        if (isIncomingType(type)) {
            delta = requestDto.getQuantity();
            newBalance = currentStock + delta;
        } else {
            delta = -requestDto.getQuantity();
            newBalance = currentStock + delta;
            if (newBalance < 0) {
                throw new IllegalArgumentException("Cannot reduce stock below zero. Current stock: " + currentStock + 
                        ", reduction requested: " + requestDto.getQuantity());
            }
        }

        product.setStockQuantity(newBalance);
        if (newBalance == 0) {
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        } else if (product.getStatus() == ProductStatus.OUT_OF_STOCK && newBalance > 0) {
            product.setStatus(ProductStatus.ACTIVE);
        }
        productRepository.save(product);

        StockTransaction tx = StockTransaction.builder()
                .product(product)
                .user(user)
                .type(type)
                .quantityChanged(delta)
                .balanceAfter(newBalance)
                .referenceType(requestDto.getReferenceType() != null ? requestDto.getReferenceType().trim() : "MANUAL_ADJUSTMENT")
                .referenceId(requestDto.getReferenceId() != null ? requestDto.getReferenceId().trim() : null)
                .notes(requestDto.getNotes() != null ? requestDto.getNotes().trim() : null)
                .build();

        StockTransaction saved = stockTransactionRepository.save(tx);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockTransactionResponseDto> getAllTransactions() {
        return stockTransactionRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<StockTransactionResponseDto> getTransactionsPaginated(int page, int size, String sortBy, String sortDir, StockTransactionType type) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy != null ? sortBy : "id"));

        Page<StockTransaction> txPage = type != null 
                ? stockTransactionRepository.findByType(type, pageable) 
                : stockTransactionRepository.findAll(pageable);

        List<StockTransactionResponseDto> content = txPage.getContent().stream()
                .map(this::mapToResponseDto)
                .toList();

        return PagedResponse.<StockTransactionResponseDto>builder()
                .content(content)
                .pageNumber(txPage.getNumber())
                .pageSize(txPage.getSize())
                .totalElements(txPage.getTotalElements())
                .totalPages(txPage.getTotalPages())
                .first(txPage.isFirst())
                .last(txPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public StockTransactionResponseDto getTransactionById(Long id) {
        StockTransaction tx = stockTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockTransaction", "id", id));
        return mapToResponseDto(tx);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockTransactionResponseDto> getTransactionsByProduct(Long productId) {
        return stockTransactionRepository.findByProductId(productId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockTransactionResponseDto> getTransactionsByUser(Long userId) {
        return stockTransactionRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private boolean isIncomingType(StockTransactionType type) {
        return type == StockTransactionType.PURCHASE ||
                type == StockTransactionType.RETURN ||
                type == StockTransactionType.INITIAL ||
                type == StockTransactionType.ADJUSTMENT;
    }

    private StockTransactionResponseDto mapToResponseDto(StockTransaction tx) {
        return StockTransactionResponseDto.builder()
                .id(tx.getId())
                .productId(tx.getProduct() != null ? tx.getProduct().getId() : null)
                .productName(tx.getProduct() != null ? tx.getProduct().getName() : null)
                .productSku(tx.getProduct() != null ? tx.getProduct().getSku() : null)
                .userId(tx.getUser() != null ? tx.getUser().getId() : null)
                .userName(tx.getUser() != null ? tx.getUser().getFullName() : null)
                .type(tx.getType())
                .quantityChanged(tx.getQuantityChanged())
                .balanceAfter(tx.getBalanceAfter())
                .referenceType(tx.getReferenceType())
                .referenceId(tx.getReferenceId())
                .notes(tx.getNotes())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}
