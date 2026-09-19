package com.vibolSEN.inventory_mgt_system.service;

import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.SupplierRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.SupplierResponseDto;

import java.util.List;

public interface SupplierService {

    SupplierResponseDto createSupplier(SupplierRequestDto requestDto);

    List<SupplierResponseDto> getAllSuppliers();

    PagedResponse<SupplierResponseDto> getSuppliersPaginated(int page, int size, String sortBy, String sortDir);

    SupplierResponseDto getSupplierById(Long id);

    SupplierResponseDto updateSupplier(Long id, SupplierRequestDto requestDto);

    void deleteSupplier(Long id);

    List<SupplierResponseDto> searchSuppliers(String keyword);
}
