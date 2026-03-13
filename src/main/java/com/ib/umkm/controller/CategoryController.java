package com.ib.umkm.controller;

import com.ib.umkm.common.ApiResponse;
import com.ib.umkm.common.PageResult;
import com.ib.umkm.dto.CategoryDto;
import com.ib.umkm.security.JwtUser;
import com.ib.umkm.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/data")
    public List<CategoryDto> categories(Authentication authentication) {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (jwtUser.getRole().contains("ADMIN")) {
            return categoryService.getAllCategories();
        }
        return categoryService.getCategoriesByOwnerId(jwtUser.getUserId());
    }

    @GetMapping("/byMerchant/{merchantId}")
    public List<CategoryDto> categoriesByMerchant(@PathVariable Long merchantId) {
        return categoryService.getCategoriesByMerchantId(merchantId);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<CategoryDto>>> categories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        PageResult<CategoryDto> result;

        if (jwtUser.getRole().contains("ADMIN")) {
            result = categoryService.findPaged(page, size, keyword);
        } else {
            result = categoryService.findPagedByOwnerId(page, size, jwtUser.getUserId(), keyword);
        }

        ApiResponse<PageResult<CategoryDto>> response =
                new ApiResponse<>(
                        true,
                        "SUCCESS",
                        "Categories fetched successfully",
                        result
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public void createCategory(@RequestBody CategoryDto request) {

        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        categoryService.createCategory(request, jwtUser.getUsername());
    }

    @GetMapping("/{id}")
    public CategoryDto getById(@PathVariable Long id) {
        CategoryDto category = categoryService.getById(id);
        return category;
    }

    @PutMapping("/{id}")
    public void updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDto category
    ) {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        category.setId(id);
        categoryService.updateCategory(category, jwtUser.getUsername());
    }

}
