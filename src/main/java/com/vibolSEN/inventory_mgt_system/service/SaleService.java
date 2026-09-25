package com.vibolSEN.inventory_mgt_system.service;

import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.SalePaymentRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.SaleRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.SaleResponseDto;
import com.vibolSEN.inventory_mgt_system.model.enums.PaymentStatus;

import java.util.List;

public interface SaleService {

    SaleResponseDto createSale(SaleRequestDto requestDto);

    List<SaleResponseDto> getAllSales();

    PagedResponse<SaleResponseDto> getSalesPaginated(int page, int size, String sortBy, String sortDir, PaymentStatus paymentStatus);

    SaleResponseDto getSaleById(Long id);

    SaleResponseDto getSaleBySaleNumber(String saleNumber);

    SaleResponseDto addPaymentToSale(Long saleId, SalePaymentRequestDto paymentRequestDto);

    SaleResponseDto updateSaleNotes(Long saleId, String notes);

    List<SaleResponseDto> searchSales(String keyword);

    List<SaleResponseDto> getSalesByCustomerId(Long customerId);

    void deleteSale(Long id);
}
