package com.ib.umkm.service;

import com.ib.umkm.common.PageResult;
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

    public PageResult<ProductDto> findPaged(int page, int size, String keyword) {
        int offset = page * size;

        List<ProductDto> categories = productRepository.findAll(size, offset, keyword);
        int total = productRepository.countAll(keyword);

        return new PageResult<>(categories, page, size, total);
    }

    public PageResult<ProductDto> findPagedByOwnerId(int page, int size, Long ownerId, String keyword) {

        int offset = page * size;

        List<ProductDto> categories = productRepository.findByOwnerId(size, offset, ownerId, keyword);
        int total = productRepository.countAllByOwnerId(ownerId, keyword);

        return new PageResult<>(categories, page, size, total);
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
