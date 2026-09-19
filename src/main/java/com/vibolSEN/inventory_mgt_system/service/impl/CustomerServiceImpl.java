package com.vibolSEN.inventory_mgt_system.service.impl;

import com.vibolSEN.inventory_mgt_system.dto.CustomerRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.CustomerResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.exception.DuplicateResourceException;
import com.vibolSEN.inventory_mgt_system.exception.ResourceInUseException;
import com.vibolSEN.inventory_mgt_system.exception.ResourceNotFoundException;
import com.vibolSEN.inventory_mgt_system.model.Customer;
import com.vibolSEN.inventory_mgt_system.repository.CustomerRepository;
import com.vibolSEN.inventory_mgt_system.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerResponseDto createCustomer(CustomerRequestDto requestDto) {
        String email = requestDto.getEmail() != null && !requestDto.getEmail().trim().isEmpty() 
                ? requestDto.getEmail().trim() : null;
        String phone = requestDto.getPhone() != null && !requestDto.getPhone().trim().isEmpty() 
                ? requestDto.getPhone().trim() : null;

        if (email != null && customerRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Customer", "email", email);
        }
        if (phone != null && customerRepository.existsByPhone(phone)) {
            throw new DuplicateResourceException("Customer", "phone", phone);
        }

        Customer customer = Customer.builder()
                .name(requestDto.getName().trim())
                .email(email)
                .phone(phone)
                .address(requestDto.getAddress() != null ? requestDto.getAddress().trim() : null)
                .notes(requestDto.getNotes() != null ? requestDto.getNotes().trim() : null)
                .loyaltyPoints(0)
                .totalSpent(BigDecimal.ZERO)
                .build();

        Customer saved = customerRepository.save(customer);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CustomerResponseDto> getCustomersPaginated(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy != null ? sortBy : "id"));

        Page<Customer> customerPage = customerRepository.findAll(pageable);
        List<CustomerResponseDto> content = customerPage.getContent().stream()
                .map(this::mapToResponseDto)
                .toList();

        return PagedResponse.<CustomerResponseDto>builder()
                .content(content)
                .pageNumber(customerPage.getNumber())
                .pageSize(customerPage.getSize())
                .totalElements(customerPage.getTotalElements())
                .totalPages(customerPage.getTotalPages())
                .first(customerPage.isFirst())
                .last(customerPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerById(Long id) {
        return mapToResponseDto(findCustomerById(id));
    }

    @Override
    @Transactional
    public CustomerResponseDto updateCustomer(Long id, CustomerRequestDto requestDto) {
        Customer customer = findCustomerById(id);

        String email = requestDto.getEmail() != null && !requestDto.getEmail().trim().isEmpty() 
                ? requestDto.getEmail().trim() : null;
        String phone = requestDto.getPhone() != null && !requestDto.getPhone().trim().isEmpty() 
                ? requestDto.getPhone().trim() : null;

        if (email != null && customerRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateResourceException("Customer", "email", email);
        }
        if (phone != null && customerRepository.existsByPhoneAndIdNot(phone, id)) {
            throw new DuplicateResourceException("Customer", "phone", phone);
        }

        customer.setName(requestDto.getName().trim());
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setAddress(requestDto.getAddress() != null ? requestDto.getAddress().trim() : null);
        customer.setNotes(requestDto.getNotes() != null ? requestDto.getNotes().trim() : null);

        Customer updated = customerRepository.save(customer);
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = findCustomerById(id);
        if (!customer.getSales().isEmpty()) {
            throw new ResourceInUseException("Customer", "id", id, "it contains associated sales history.");
        }
        customerRepository.delete(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerRepository.searchCustomers(keyword.trim()).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public CustomerResponseDto adjustLoyaltyPoints(Long id, int points) {
        Customer customer = findCustomerById(id);
        int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
        customer.setLoyaltyPoints(Math.max(0, currentPoints + points));
        Customer updated = customerRepository.save(customer);
        return mapToResponseDto(updated);
    }

    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
    }

    private CustomerResponseDto mapToResponseDto(Customer customer) {
        return CustomerResponseDto.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .loyaltyPoints(customer.getLoyaltyPoints())
                .totalSpent(customer.getTotalSpent())
                .notes(customer.getNotes())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
