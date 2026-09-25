package com.vibolSEN.inventory_mgt_system.service.impl;

import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.SaleItemRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.SaleItemResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.SalePaymentRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.SalePaymentResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.SaleRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.SaleResponseDto;
import com.vibolSEN.inventory_mgt_system.exception.ResourceNotFoundException;
import com.vibolSEN.inventory_mgt_system.model.Customer;
import com.vibolSEN.inventory_mgt_system.model.Product;
import com.vibolSEN.inventory_mgt_system.model.Sale;
import com.vibolSEN.inventory_mgt_system.model.SaleItem;
import com.vibolSEN.inventory_mgt_system.model.SalePayment;
import com.vibolSEN.inventory_mgt_system.model.StockTransaction;
import com.vibolSEN.inventory_mgt_system.model.User;
import com.vibolSEN.inventory_mgt_system.model.enums.CartStatus;
import com.vibolSEN.inventory_mgt_system.model.enums.DiscountType;
import com.vibolSEN.inventory_mgt_system.model.enums.PaymentStatus;
import com.vibolSEN.inventory_mgt_system.model.enums.ProductStatus;
import com.vibolSEN.inventory_mgt_system.model.enums.StockTransactionType;
import com.vibolSEN.inventory_mgt_system.repository.CartRepository;
import com.vibolSEN.inventory_mgt_system.repository.CustomerRepository;
import com.vibolSEN.inventory_mgt_system.repository.ProductRepository;
import com.vibolSEN.inventory_mgt_system.repository.SaleItemRepository;
import com.vibolSEN.inventory_mgt_system.repository.SalePaymentRepository;
import com.vibolSEN.inventory_mgt_system.repository.SaleRepository;
import com.vibolSEN.inventory_mgt_system.repository.StockTransactionRepository;
import com.vibolSEN.inventory_mgt_system.repository.UserRepository;
import com.vibolSEN.inventory_mgt_system.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final SalePaymentRepository salePaymentRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final StockTransactionRepository stockTransactionRepository;

    @Override
    @Transactional
    public SaleResponseDto createSale(SaleRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestDto.getUserId()));

        Customer customer = null;
        String customerName = requestDto.getCustomerName();
        if (requestDto.getCustomerId() != null) {
            customer = customerRepository.findById(requestDto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", requestDto.getCustomerId()));
            if (customerName == null || customerName.trim().isEmpty()) {
                customerName = customer.getName();
            }
        }

        if (requestDto.getCartId() != null) {
            cartRepository.findById(requestDto.getCartId()).ifPresent(cart -> {
                cart.setStatus(CartStatus.CHECKED_OUT);
                cartRepository.save(cart);
            });
        }

        String saleNumber = generateSaleNumber();
        BigDecimal subtotal = BigDecimal.ZERO;

        List<SaleItem> saleItems = new ArrayList<>();
        for (SaleItemRequestDto itemDto : requestDto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDto.getProductId()));

            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product '" + product.getName() + 
                        "' (SKU: " + product.getSku() + "). Requested: " + itemDto.getQuantity() + 
                        ", Available: " + product.getStockQuantity());
            }

            BigDecimal unitPrice = itemDto.getUnitPrice() != null ? itemDto.getUnitPrice() : product.getUnitPrice();
            BigDecimal itemDiscount = itemDto.getDiscountAmount() != null ? itemDto.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity())).subtract(itemDiscount);

            subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity())));

            int newStock = product.getStockQuantity() - itemDto.getQuantity();
            product.setStockQuantity(newStock);
            if (newStock == 0) {
                product.setStatus(ProductStatus.OUT_OF_STOCK);
            }
            productRepository.save(product);

            StockTransaction stockTx = StockTransaction.builder()
                    .product(product)
                    .user(user)
                    .type(StockTransactionType.SALE)
                    .quantityChanged(-itemDto.getQuantity())
                    .balanceAfter(newStock)
                    .referenceType("SALE")
                    .referenceId(saleNumber)
                    .notes("POS checkout sale: " + saleNumber)
                    .build();
            stockTransactionRepository.save(stockTx);

            SaleItem saleItem = SaleItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .quantity(itemDto.getQuantity())
                    .unitPrice(unitPrice)
                    .discountAmount(itemDiscount)
                    .lineTotal(lineTotal)
                    .build();
            saleItems.add(saleItem);
        }

        DiscountType discountType = requestDto.getDiscountType() != null ? requestDto.getDiscountType() : DiscountType.NONE;
        BigDecimal discountValue = requestDto.getDiscountValue() != null ? requestDto.getDiscountValue() : BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (discountType == DiscountType.PERCENTAGE) {
            discountAmount = subtotal.multiply(discountValue).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (discountType == DiscountType.FIXED_AMOUNT) {
            discountAmount = discountValue;
        }

        BigDecimal taxAmount = requestDto.getTaxAmount() != null ? requestDto.getTaxAmount() : BigDecimal.ZERO;
        BigDecimal totalAmount = subtotal.subtract(discountAmount).add(taxAmount);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        List<SalePayment> salePayments = new ArrayList<>();
        BigDecimal paidAmount = BigDecimal.ZERO;

        if (requestDto.getPayments() != null && !requestDto.getPayments().isEmpty()) {
            for (SalePaymentRequestDto pDto : requestDto.getPayments()) {
                paidAmount = paidAmount.add(pDto.getAmount());
                SalePayment payment = SalePayment.builder()
                        .paymentMethod(pDto.getPaymentMethod())
                        .amount(pDto.getAmount())
                        .referenceNumber(pDto.getReferenceNumber())
                        .notes(pDto.getNotes())
                        .build();
                salePayments.add(payment);
            }
        } else if (requestDto.getPaidAmount() != null) {
            paidAmount = requestDto.getPaidAmount();
        }

        BigDecimal changeAmount = paidAmount.compareTo(totalAmount) > 0 
                ? paidAmount.subtract(totalAmount) 
                : BigDecimal.ZERO;

        PaymentStatus paymentStatus = PaymentStatus.PENDING;
        if (paidAmount.compareTo(totalAmount) >= 0 && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            paymentStatus = PaymentStatus.PAID;
        } else if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            paymentStatus = PaymentStatus.PARTIAL;
        }

        Sale sale = Sale.builder()
                .saleNumber(saleNumber)
                .customer(customer)
                .user(user)
                .customerName(customerName)
                .subtotal(subtotal)
                .discountType(discountType)
                .discountValue(discountValue)
                .discountAmount(discountAmount)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .paidAmount(paidAmount)
                .changeAmount(changeAmount)
                .paymentStatus(paymentStatus)
                .notes(requestDto.getNotes() != null ? requestDto.getNotes().trim() : null)
                .saleItems(new ArrayList<>())
                .salePayments(new ArrayList<>())
                .build();

        Sale savedSale = saleRepository.save(sale);

        for (SaleItem item : saleItems) {
            item.setSale(savedSale);
            saleItemRepository.save(item);
            savedSale.getSaleItems().add(item);
        }

        for (SalePayment payment : salePayments) {
            payment.setSale(savedSale);
            salePaymentRepository.save(payment);
            savedSale.getSalePayments().add(payment);
        }

        if (customer != null) {
            BigDecimal currentSpent = customer.getTotalSpent() != null ? customer.getTotalSpent() : BigDecimal.ZERO;
            customer.setTotalSpent(currentSpent.add(totalAmount));

            int earnedPoints = totalAmount.divide(BigDecimal.valueOf(10), 0, RoundingMode.DOWN).intValue();
            int currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
            customer.setLoyaltyPoints(currentPoints + earnedPoints);

            customerRepository.save(customer);
        }

        return mapToResponseDto(savedSale);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDto> getAllSales() {
        return saleRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SaleResponseDto> getSalesPaginated(int page, int size, String sortBy, String sortDir, PaymentStatus paymentStatus) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy != null ? sortBy : "id"));

        Page<Sale> salePage = paymentStatus != null 
                ? saleRepository.findByPaymentStatus(paymentStatus, pageable) 
                : saleRepository.findAll(pageable);

        List<SaleResponseDto> content = salePage.getContent().stream()
                .map(this::mapToResponseDto)
                .toList();

        return PagedResponse.<SaleResponseDto>builder()
                .content(content)
                .pageNumber(salePage.getNumber())
                .pageSize(salePage.getSize())
                .totalElements(salePage.getTotalElements())
                .totalPages(salePage.getTotalPages())
                .first(salePage.isFirst())
                .last(salePage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponseDto getSaleById(Long id) {
        return mapToResponseDto(findSaleById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponseDto getSaleBySaleNumber(String saleNumber) {
        Sale sale = saleRepository.findBySaleNumberIgnoreCase(saleNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Sale", "saleNumber", saleNumber));
        return mapToResponseDto(sale);
    }

    @Override
    @Transactional
    public SaleResponseDto addPaymentToSale(Long saleId, SalePaymentRequestDto paymentRequestDto) {
        Sale sale = findSaleById(saleId);

        SalePayment payment = SalePayment.builder()
                .sale(sale)
                .paymentMethod(paymentRequestDto.getPaymentMethod())
                .amount(paymentRequestDto.getAmount())
                .referenceNumber(paymentRequestDto.getReferenceNumber())
                .notes(paymentRequestDto.getNotes())
                .build();
        salePaymentRepository.save(payment);

        BigDecimal newPaidAmount = sale.getPaidAmount().add(paymentRequestDto.getAmount());
        sale.setPaidAmount(newPaidAmount);

        if (newPaidAmount.compareTo(sale.getTotalAmount()) >= 0) {
            sale.setPaymentStatus(PaymentStatus.PAID);
            sale.setChangeAmount(newPaidAmount.subtract(sale.getTotalAmount()));
        } else {
            sale.setPaymentStatus(PaymentStatus.PARTIAL);
        }

        Sale updated = saleRepository.save(sale);
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional
    public SaleResponseDto updateSaleNotes(Long saleId, String notes) {
        Sale sale = findSaleById(saleId);
        sale.setNotes(notes != null ? notes.trim() : null);
        Sale updated = saleRepository.save(sale);
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDto> searchSales(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSales();
        }
        return saleRepository.searchSales(keyword.trim()).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponseDto> getSalesByCustomerId(Long customerId) {
        return saleRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteSale(Long id) {
        Sale sale = findSaleById(id);

        if (sale.getSaleItems() != null) {
            for (SaleItem item : sale.getSaleItems()) {
                Product product = item.getProduct();
                if (product != null) {
                    int restoredStock = product.getStockQuantity() + item.getQuantity();
                    product.setStockQuantity(restoredStock);
                    if (product.getStatus() == ProductStatus.OUT_OF_STOCK && restoredStock > 0) {
                        product.setStatus(ProductStatus.ACTIVE);
                    }
                    productRepository.save(product);

                    StockTransaction stockTx = StockTransaction.builder()
                            .product(product)
                            .user(sale.getUser())
                            .type(StockTransactionType.RETURN)
                            .quantityChanged(item.getQuantity())
                            .balanceAfter(restoredStock)
                            .referenceType("SALE_DELETION")
                            .referenceId(sale.getSaleNumber())
                            .notes("Reverted stock from deleted sale: " + sale.getSaleNumber())
                            .build();
                    stockTransactionRepository.save(stockTx);
                }
                saleItemRepository.delete(item);
            }
        }

        if (sale.getSalePayments() != null) {
            for (SalePayment payment : sale.getSalePayments()) {
                salePaymentRepository.delete(payment);
            }
        }

        saleRepository.delete(sale);
    }

    private Sale findSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", "id", id));
    }

    private String generateSaleNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "SAL-" + timestamp + "-" + rand;
    }

    private SaleResponseDto mapToResponseDto(Sale sale) {
        List<SaleItemResponseDto> itemDtos = new ArrayList<>();
        if (sale.getSaleItems() != null) {
            for (SaleItem item : sale.getSaleItems()) {
                itemDtos.add(SaleItemResponseDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProductName())
                        .productSku(item.getProductSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .discountAmount(item.getDiscountAmount())
                        .lineTotal(item.getLineTotal())
                        .build());
            }
        }

        List<SalePaymentResponseDto> paymentDtos = new ArrayList<>();
        if (sale.getSalePayments() != null) {
            for (SalePayment payment : sale.getSalePayments()) {
                paymentDtos.add(SalePaymentResponseDto.builder()
                        .id(payment.getId())
                        .paymentMethod(payment.getPaymentMethod())
                        .amount(payment.getAmount())
                        .referenceNumber(payment.getReferenceNumber())
                        .notes(payment.getNotes())
                        .createdAt(payment.getCreatedAt())
                        .build());
            }
        }

        return SaleResponseDto.builder()
                .id(sale.getId())
                .saleNumber(sale.getSaleNumber())
                .customerId(sale.getCustomer() != null ? sale.getCustomer().getId() : null)
                .customerName(sale.getCustomerName())
                .userId(sale.getUser() != null ? sale.getUser().getId() : null)
                .userName(sale.getUser() != null ? sale.getUser().getFullName() : null)
                .subtotal(sale.getSubtotal())
                .discountType(sale.getDiscountType())
                .discountValue(sale.getDiscountValue())
                .discountAmount(sale.getDiscountAmount())
                .taxAmount(sale.getTaxAmount())
                .totalAmount(sale.getTotalAmount())
                .paidAmount(sale.getPaidAmount())
                .changeAmount(sale.getChangeAmount())
                .paymentStatus(sale.getPaymentStatus())
                .notes(sale.getNotes())
                .items(itemDtos)
                .payments(paymentDtos)
                .createdAt(sale.getCreatedAt())
                .updatedAt(sale.getUpdatedAt())
                .build();
    }
}
