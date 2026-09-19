package com.vibolSEN.inventory_mgt_system.service.impl;

import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.SupplierRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.SupplierResponseDto;
import com.vibolSEN.inventory_mgt_system.exception.DuplicateResourceException;
import com.vibolSEN.inventory_mgt_system.exception.ResourceInUseException;
import com.vibolSEN.inventory_mgt_system.exception.ResourceNotFoundException;
import com.vibolSEN.inventory_mgt_system.model.Supplier;
import com.vibolSEN.inventory_mgt_system.repository.SupplierRepository;
import com.vibolSEN.inventory_mgt_system.service.SupplierService;
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
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public SupplierResponseDto createSupplier(SupplierRequestDto requestDto) {
        String trimmedName = requestDto.getName().trim();
        if (supplierRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("Supplier", "name", trimmedName);
        }

        Supplier supplier = Supplier.builder()
                .name(trimmedName)
                .contactPerson(requestDto.getContactPerson() != null ? requestDto.getContactPerson().trim() : null)
                .email(requestDto.getEmail() != null ? requestDto.getEmail().trim() : null)
                .phone(requestDto.getPhone() != null ? requestDto.getPhone().trim() : null)
                .address(requestDto.getAddress() != null ? requestDto.getAddress().trim() : null)
                .paymentTerms(requestDto.getPaymentTerms() != null ? requestDto.getPaymentTerms().trim() : null)
                .notes(requestDto.getNotes() != null ? requestDto.getNotes().trim() : null)
                .build();

        Supplier saved = supplierRepository.save(supplier);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SupplierResponseDto> getSuppliersPaginated(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy != null ? sortBy : "id"));

        Page<Supplier> supplierPage = supplierRepository.findAll(pageable);
        List<SupplierResponseDto> content = supplierPage.getContent().stream()
                .map(this::mapToResponseDto)
                .toList();

        return PagedResponse.<SupplierResponseDto>builder()
                .content(content)
                .pageNumber(supplierPage.getNumber())
                .pageSize(supplierPage.getSize())
                .totalElements(supplierPage.getTotalElements())
                .totalPages(supplierPage.getTotalPages())
                .first(supplierPage.isFirst())
                .last(supplierPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponseDto getSupplierById(Long id) {
        return mapToResponseDto(findSupplierById(id));
    }

    @Override
    @Transactional
    public SupplierResponseDto updateSupplier(Long id, SupplierRequestDto requestDto) {
        Supplier supplier = findSupplierById(id);
        String trimmedName = requestDto.getName().trim();

        if (supplierRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new DuplicateResourceException("Supplier", "name", trimmedName);
        }

        supplier.setName(trimmedName);
        supplier.setContactPerson(requestDto.getContactPerson() != null ? requestDto.getContactPerson().trim() : null);
        supplier.setEmail(requestDto.getEmail() != null ? requestDto.getEmail().trim() : null);
        supplier.setPhone(requestDto.getPhone() != null ? requestDto.getPhone().trim() : null);
        supplier.setAddress(requestDto.getAddress() != null ? requestDto.getAddress().trim() : null);
        supplier.setPaymentTerms(requestDto.getPaymentTerms() != null ? requestDto.getPaymentTerms().trim() : null);
        supplier.setNotes(requestDto.getNotes() != null ? requestDto.getNotes().trim() : null);

        Supplier updated = supplierRepository.save(supplier);
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        Supplier supplier = findSupplierById(id);
        if (!supplier.getProducts().isEmpty() || !supplier.getShipments().isEmpty()) {
            throw new ResourceInUseException("Supplier", "id", id, "it contains associated products or shipments.");
        }
        supplierRepository.delete(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponseDto> searchSuppliers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSuppliers();
        }
        return supplierRepository.searchSuppliers(keyword.trim()).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private Supplier findSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
    }

    private SupplierResponseDto mapToResponseDto(Supplier supplier) {
        return SupplierResponseDto.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .paymentTerms(supplier.getPaymentTerms())
                .notes(supplier.getNotes())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }
}
