package com.ib.umkm.controller;

import com.ib.umkm.dto.CategoryDto;
import com.ib.umkm.security.JwtUser;
import com.ib.umkm.service.CategoryService;
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

    @GetMapping
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
        return categoryService.getById(id);
    }

    @PutMapping("/{id}")
    public void updateUser(
            @PathVariable Long id,
            @RequestBody CategoryDto category
    ) {
        String username = (String) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        category.setId(id);
        categoryService.updateCategory(category, username);
    }

}
