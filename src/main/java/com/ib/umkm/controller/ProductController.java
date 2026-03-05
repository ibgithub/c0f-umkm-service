package com.ib.umkm.controller;

import com.ib.umkm.common.ApiResponse;
import com.ib.umkm.common.PageResult;
import com.ib.umkm.dto.ProductDto;
import com.ib.umkm.security.JwtUser;
import com.ib.umkm.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<ProductDto>>> categories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        PageResult<ProductDto> result;

        if (jwtUser.getRole().contains("ADMIN")) {
            result = productService.findPaged(page, size, keyword);
        } else {
            result = productService.findPagedByOwnerId(page, size, jwtUser.getUserId(), keyword);
        }

        ApiResponse<PageResult<ProductDto>> response =
                new ApiResponse<>(
                        true,
                        "SUCCESS",
                        "Products fetched successfully",
                        result
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public void createProduct(@RequestBody ProductDto product) {

        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        productService.createProduct(product, jwtUser.getUsername());
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PutMapping("/{id}")
    public void updateUser(
            @PathVariable Long id,
            @RequestBody ProductDto product
    ) {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        product.setId(id);
        productService.updateProduct(product, jwtUser.getUsername());
    }

}
