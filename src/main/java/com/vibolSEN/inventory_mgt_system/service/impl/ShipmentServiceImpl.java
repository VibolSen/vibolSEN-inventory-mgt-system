package com.vibolSEN.inventory_mgt_system.service.impl;

import com.vibolSEN.inventory_mgt_system.dto.PagedResponse;
import com.vibolSEN.inventory_mgt_system.dto.ShipmentItemReceiveDto;
import com.vibolSEN.inventory_mgt_system.dto.ShipmentItemRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.ShipmentItemResponseDto;
import com.vibolSEN.inventory_mgt_system.dto.ShipmentReceiveRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.ShipmentRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.ShipmentResponseDto;
import com.vibolSEN.inventory_mgt_system.exception.ResourceInUseException;
import com.vibolSEN.inventory_mgt_system.exception.ResourceNotFoundException;
import com.vibolSEN.inventory_mgt_system.model.Product;
import com.vibolSEN.inventory_mgt_system.model.Shipment;
import com.vibolSEN.inventory_mgt_system.model.ShipmentItem;
import com.vibolSEN.inventory_mgt_system.model.StockTransaction;
import com.vibolSEN.inventory_mgt_system.model.Supplier;
import com.vibolSEN.inventory_mgt_system.model.User;
import com.vibolSEN.inventory_mgt_system.model.enums.ProductStatus;
import com.vibolSEN.inventory_mgt_system.model.enums.ShipmentItemStatus;
import com.vibolSEN.inventory_mgt_system.model.enums.ShipmentStatus;
import com.vibolSEN.inventory_mgt_system.model.enums.StockTransactionType;
import com.vibolSEN.inventory_mgt_system.repository.ProductRepository;
import com.vibolSEN.inventory_mgt_system.repository.ShipmentItemRepository;
import com.vibolSEN.inventory_mgt_system.repository.ShipmentRepository;
import com.vibolSEN.inventory_mgt_system.repository.StockTransactionRepository;
import com.vibolSEN.inventory_mgt_system.repository.SupplierRepository;
import com.vibolSEN.inventory_mgt_system.repository.UserRepository;
import com.vibolSEN.inventory_mgt_system.service.ShipmentService;
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
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentItemRepository shipmentItemRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StockTransactionRepository stockTransactionRepository;

    @Override
    @Transactional
    public ShipmentResponseDto createShipment(ShipmentRequestDto requestDto) {
        Supplier supplier = supplierRepository.findById(requestDto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", requestDto.getSupplierId()));

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestDto.getUserId()));

        String shipmentNumber = generateShipmentNumber();
        BigDecimal totalCost = BigDecimal.ZERO;

        List<ShipmentItem> items = new ArrayList<>();
        for (ShipmentItemRequestDto itemDto : requestDto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemDto.getProductId()));

            BigDecimal itemTotalCost = itemDto.getUnitCost().multiply(BigDecimal.valueOf(itemDto.getOrderedQuantity()));
            totalCost = totalCost.add(itemTotalCost);

            ShipmentItem item = ShipmentItem.builder()
                    .product(product)
                    .orderedQuantity(itemDto.getOrderedQuantity())
                    .unitCost(itemDto.getUnitCost())
                    .itemStatus(ShipmentItemStatus.PENDING)
                    .notes(itemDto.getNotes() != null ? itemDto.getNotes().trim() : null)
                    .build();
            items.add(item);
        }

        Shipment shipment = Shipment.builder()
                .shipmentNumber(shipmentNumber)
                .trackingNumber(requestDto.getTrackingNumber() != null ? requestDto.getTrackingNumber().trim() : null)
                .supplier(supplier)
                .user(user)
                .status(ShipmentStatus.ORDERED)
                .expectedDate(requestDto.getExpectedDate())
                .totalCost(totalCost)
                .notes(requestDto.getNotes() != null ? requestDto.getNotes().trim() : null)
                .shipmentItems(new ArrayList<>())
                .build();

        Shipment savedShipment = shipmentRepository.save(shipment);

        for (ShipmentItem item : items) {
            item.setShipment(savedShipment);
            shipmentItemRepository.save(item);
            savedShipment.getShipmentItems().add(item);
        }

        return mapToResponseDto(savedShipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentResponseDto> getAllShipments() {
        return shipmentRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ShipmentResponseDto> getShipmentsPaginated(int page, int size, String sortBy, String sortDir, ShipmentStatus status) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy != null ? sortBy : "id"));

        Page<Shipment> shipmentPage = status != null 
                ? shipmentRepository.findByStatus(status, pageable) 
                : shipmentRepository.findAll(pageable);

        List<ShipmentResponseDto> content = shipmentPage.getContent().stream()
                .map(this::mapToResponseDto)
                .toList();

        return PagedResponse.<ShipmentResponseDto>builder()
                .content(content)
                .pageNumber(shipmentPage.getNumber())
                .pageSize(shipmentPage.getSize())
                .totalElements(shipmentPage.getTotalElements())
                .totalPages(shipmentPage.getTotalPages())
                .first(shipmentPage.isFirst())
                .last(shipmentPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponseDto getShipmentById(Long id) {
        return mapToResponseDto(findShipmentById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponseDto getShipmentByNumber(String shipmentNumber) {
        Shipment shipment = shipmentRepository.findByShipmentNumberIgnoreCase(shipmentNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "shipmentNumber", shipmentNumber));
        return mapToResponseDto(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipmentResponseDto> getShipmentsBySupplier(Long supplierId) {
        return shipmentRepository.findBySupplierId(supplierId).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ShipmentResponseDto updateShipment(Long id, ShipmentRequestDto requestDto) {
        Shipment shipment = findShipmentById(id);

        if (shipment.getStatus() == ShipmentStatus.RECEIVED) {
            throw new IllegalStateException("Cannot update a shipment that has already been received");
        }

        Supplier supplier = supplierRepository.findById(requestDto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", requestDto.getSupplierId()));
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestDto.getUserId()));

        shipment.setSupplier(supplier);
        shipment.setUser(user);
        shipment.setTrackingNumber(requestDto.getTrackingNumber() != null ? requestDto.getTrackingNumber().trim() : null);
        shipment.setExpectedDate(requestDto.getExpectedDate());
        shipment.setNotes(requestDto.getNotes() != null ? requestDto.getNotes().trim() : null);

        Shipment updated = shipmentRepository.save(shipment);
        return mapToResponseDto(updated);
    }

    @Override
    @Transactional
    public ShipmentResponseDto receiveShipment(Long id, ShipmentReceiveRequestDto receiveRequestDto) {
        Shipment shipment = findShipmentById(id);

        if (shipment.getStatus() == ShipmentStatus.RECEIVED) {
            throw new IllegalStateException("Shipment has already been marked as RECEIVED");
        }
        if (shipment.getStatus() == ShipmentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot receive a cancelled shipment");
        }

        Map<Long, ShipmentItemReceiveDto> receiveMap = receiveRequestDto.getItems().stream()
                .collect(Collectors.toMap(ShipmentItemReceiveDto::getShipmentItemId, itemDto -> itemDto));

        boolean allReceived = true;

        for (ShipmentItem item : shipment.getShipmentItems()) {
            ShipmentItemReceiveDto rx = receiveMap.get(item.getId());
            if (rx != null) {
                int newlyReceived = rx.getReceivedQuantity();
                int damaged = rx.getDamagedQuantity() != null ? rx.getDamagedQuantity() : 0;

                item.setReceivedQuantity(item.getReceivedQuantity() + newlyReceived);
                item.setDamagedQuantity(item.getDamagedQuantity() + damaged);
                if (rx.getNotes() != null) {
                    item.setNotes(rx.getNotes().trim());
                }

                if (item.getReceivedQuantity() >= item.getOrderedQuantity()) {
                    item.setItemStatus(ShipmentItemStatus.RECEIVED);
                } else if (item.getReceivedQuantity() > 0) {
                    item.setItemStatus(ShipmentItemStatus.PARTIAL);
                } else if (damaged > 0) {
                    item.setItemStatus(ShipmentItemStatus.DAMAGED);
                }

                if (newlyReceived > 0) {
                    Product product = item.getProduct();
                    int updatedStock = product.getStockQuantity() + newlyReceived;
                    product.setStockQuantity(updatedStock);
                    if (product.getStatus() == ProductStatus.OUT_OF_STOCK) {
                        product.setStatus(ProductStatus.ACTIVE);
                    }
                    productRepository.save(product);

                    StockTransaction tx = StockTransaction.builder()
                            .product(product)
                            .user(shipment.getUser())
                            .type(StockTransactionType.PURCHASE)
                            .quantityChanged(newlyReceived)
                            .balanceAfter(updatedStock)
                            .referenceType("SHIPMENT")
                            .referenceId(shipment.getShipmentNumber())
                            .notes("Shipment received: " + shipment.getShipmentNumber())
                            .build();
                    stockTransactionRepository.save(tx);
                }

                shipmentItemRepository.save(item);
            }

            if (item.getReceivedQuantity() < item.getOrderedQuantity()) {
                allReceived = false;
            }
        }

        if (allReceived) {
            shipment.setStatus(ShipmentStatus.RECEIVED);
        }

        shipment.setReceivedDate(LocalDateTime.now());
        if (receiveRequestDto.getNotes() != null) {
            shipment.setNotes(receiveRequestDto.getNotes().trim());
        }

        Shipment saved = shipmentRepository.save(shipment);
        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public void deleteShipment(Long id) {
        Shipment shipment = findShipmentById(id);
        if (shipment.getStatus() == ShipmentStatus.RECEIVED) {
            throw new ResourceInUseException("Shipment", "id", id, 
                    "it has already been received into inventory and cannot be deleted.");
        }
        for (ShipmentItem item : shipment.getShipmentItems()) {
            shipmentItemRepository.delete(item);
        }
        shipmentRepository.delete(shipment);
    }

    private Shipment findShipmentById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", id));
    }

    private String generateShipmentNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "SHP-" + timestamp + "-" + rand;
    }

    private ShipmentResponseDto mapToResponseDto(Shipment shipment) {
        List<ShipmentItemResponseDto> itemDtos = new ArrayList<>();
        if (shipment.getShipmentItems() != null) {
            for (ShipmentItem item : shipment.getShipmentItems()) {
                BigDecimal totalItemCost = item.getUnitCost().multiply(BigDecimal.valueOf(item.getOrderedQuantity()));
                itemDtos.add(ShipmentItemResponseDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                        .productSku(item.getProduct() != null ? item.getProduct().getSku() : null)
                        .orderedQuantity(item.getOrderedQuantity())
                        .receivedQuantity(item.getReceivedQuantity())
                        .damagedQuantity(item.getDamagedQuantity())
                        .unitCost(item.getUnitCost())
                        .totalCost(totalItemCost)
                        .itemStatus(item.getItemStatus())
                        .notes(item.getNotes())
                        .build());
            }
        }

        return ShipmentResponseDto.builder()
                .id(shipment.getId())
                .shipmentNumber(shipment.getShipmentNumber())
                .trackingNumber(shipment.getTrackingNumber())
                .supplierId(shipment.getSupplier() != null ? shipment.getSupplier().getId() : null)
                .supplierName(shipment.getSupplier() != null ? shipment.getSupplier().getName() : null)
                .userId(shipment.getUser() != null ? shipment.getUser().getId() : null)
                .userName(shipment.getUser() != null ? shipment.getUser().getFullName() : null)
                .status(shipment.getStatus())
                .expectedDate(shipment.getExpectedDate())
                .receivedDate(shipment.getReceivedDate())
                .totalCost(shipment.getTotalCost())
                .notes(shipment.getNotes())
                .items(itemDtos)
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .build();
    }
}
