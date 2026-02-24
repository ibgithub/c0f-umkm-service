package com.ib.umkm.service;

import com.ib.umkm.dto.CategoryDto;
import com.ib.umkm.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll();
    }
    public List<CategoryDto> getCategoriesByOwnerId(Long merchantId) {
        return categoryRepository.findByOwnerId(merchantId);
    }

    public CategoryDto getById(Long id) {
        return categoryRepository.findById(id);
    }
    public void createCategory(CategoryDto request, String loginUser) {
        request.setCreatedBy(loginUser);
        categoryRepository.insert(request);
    }
    public void updateCategory(
            CategoryDto request,
            String loginUser
    ) {
        request.setUpdatedBy(loginUser);
        categoryRepository.update(request);
    }
}
