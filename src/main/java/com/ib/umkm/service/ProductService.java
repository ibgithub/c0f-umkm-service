package com.ib.umkm.service;

import com.ib.umkm.dto.ProductDto;
import com.ib.umkm.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll();
    }
    public List<ProductDto> getProductsByOwnerId(Long userId) {
        return productRepository.findByOwnerId(userId);
    }

    public ProductDto getById(Long id) {
        return productRepository.findById(id);
    }
    public void createProduct(ProductDto request, String loginUser) {
        request.setCreatedBy(loginUser);
        productRepository.insert(request);

        //insert into
    }
    public void updateProduct(
            ProductDto request,
            String loginUser
    ) {
        request.setUpdatedBy(loginUser);
        productRepository.update(request);

    }
}
