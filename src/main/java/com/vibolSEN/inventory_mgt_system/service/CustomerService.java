package com.vibolSEN.inventory_mgt_system.service;

import com.vibolSEN.inventory_mgt_system.dto.CustomerRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.CustomerResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponseDto createCustomer(CustomerRequestDto requestDto);

    List<CustomerResponseDto> getAllCustomers();

    PagedResponse<CustomerResponseDto> getCustomersPaginated(int page, int size, String sortBy, String sortDir);

    CustomerResponseDto getCustomerById(Long id);

    CustomerResponseDto updateCustomer(Long id, CustomerRequestDto requestDto);

    void deleteCustomer(Long id);

    List<CustomerResponseDto> searchCustomers(String keyword);

    CustomerResponseDto adjustLoyaltyPoints(Long id, int points);
}
