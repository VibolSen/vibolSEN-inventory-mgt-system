package com.vibolSEN.inventory_mgt_system.service.impl;

import com.vibolSEN.inventory_mgt_system.dto.CategoryRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.CategoryResponseDto;
import com.vibolSEN.inventory_mgt_system.exception.DuplicateResourceException;
import com.vibolSEN.inventory_mgt_system.exception.ResourceNotFoundException;
import com.vibolSEN.inventory_mgt_system.model.Category;
import com.vibolSEN.inventory_mgt_system.repository.CategoryRepository;
import com.vibolSEN.inventory_mgt_system.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        String trimmedName = requestDto.getName().trim();

        if (categoryRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("Category", "name", trimmedName);
        }

        Category category = Category.builder()
                .name(trimmedName)
                .description(requestDto.getDescription() != null ? requestDto.getDescription().trim() : null)
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = findCategoryById(id);
        return mapToResponseDto(category);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto) {
        Category category = findCategoryById(id);
        String trimmedName = requestDto.getName().trim();

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new DuplicateResourceException("Category", "name", trimmedName);
        }

        category.setName(trimmedName);
        category.setDescription(requestDto.getDescription() != null ? requestDto.getDescription().trim() : null);

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponseDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = findCategoryById(id);
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> searchCategoriesByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCategories();
        }
        return categoryRepository.findByNameContainingIgnoreCase(keyword.trim()).stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    private CategoryResponseDto mapToResponseDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
