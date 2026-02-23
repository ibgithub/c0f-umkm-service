package com.ib.umkm.controller;

import com.ib.umkm.dto.ProductDto;
import com.ib.umkm.security.JwtUser;
import com.ib.umkm.service.ProductService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductDto> products() {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (jwtUser.getRole().contains("ADMIN")) {
            return productService.getAllProducts();
        }

        return productService.getProductsByOwnerId(jwtUser.getUserId());
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
