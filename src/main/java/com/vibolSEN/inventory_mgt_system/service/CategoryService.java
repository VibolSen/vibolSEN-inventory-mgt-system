package com.vibolSEN.inventory_mgt_system.service;

import com.vibolSEN.inventory_mgt_system.dto.CategoryRequestDto;
import com.vibolSEN.inventory_mgt_system.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto createCategory(CategoryRequestDto requestDto);

    List<CategoryResponseDto> getAllCategories();

    CategoryResponseDto getCategoryById(Long id);

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto requestDto);

    void deleteCategory(Long id);

    List<CategoryResponseDto> searchCategoriesByName(String keyword);
}
