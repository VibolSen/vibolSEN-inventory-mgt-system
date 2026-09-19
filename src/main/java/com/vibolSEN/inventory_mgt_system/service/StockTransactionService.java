package com.vibolSEN.inventory_mgt_system.service;

import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.StockAdjustmentRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.StockTransactionResponseDto;
import com.vibolSEN.inventory_mgt_system.model.enums.StockTransactionType;

import java.util.List;

public interface StockTransactionService {

    StockTransactionResponseDto adjustStock(StockAdjustmentRequestDto requestDto);

    List<StockTransactionResponseDto> getAllTransactions();

    PagedResponse<StockTransactionResponseDto> getTransactionsPaginated(int page, int size, String sortBy, String sortDir, StockTransactionType type);

    StockTransactionResponseDto getTransactionById(Long id);

    List<StockTransactionResponseDto> getTransactionsByProduct(Long productId);

    List<StockTransactionResponseDto> getTransactionsByUser(Long userId);
}
