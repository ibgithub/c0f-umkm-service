package com.ib.umkm.service;

import com.ib.umkm.common.PageResult;
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

    public List<CategoryDto> getCategoriesByOwnerId(Long userId) {
        return categoryRepository.findByOwnerId(userId);
    }
    public List<CategoryDto> getCategoriesByMerchantId(Long merchantId) {
        return categoryRepository.findByMerchantId(merchantId);
    }
    public PageResult<CategoryDto> findPaged(int page, int size, String keyword) {
        int offset = page * size;

        List<CategoryDto> categories = categoryRepository.findAll(size, offset, keyword);
        int total = categoryRepository.countAll(keyword);

        return new PageResult<>(categories, page, size, total);
    }

    public PageResult<CategoryDto> findPagedByOwnerId(int page, int size, Long ownerId, String keyword) {

        int offset = page * size;

        List<CategoryDto> categories = categoryRepository.findByOwnerId(size, offset, ownerId, keyword);
        int total = categoryRepository.countAllByOwnerId(ownerId, keyword);

        return new PageResult<>(categories, page, size, total);
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
