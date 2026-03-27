package com.demo.service.oderservice;

import com.demo.model.Product;
import com.demo.model.dto.CreateProductRequest;
import com.demo.model.dto.ProductResponseDto;
import com.demo.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponseDto createProduct(CreateProductRequest request) {

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);

        return mapToDto(saved);
    }

    public Page<ProductResponseDto> getAllProducts(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return productRepository.findAll(pageable)
                .map(this::mapToDto);
    }

    public ProductResponseDto updateProduct(String id, CreateProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setUpdatedAt(LocalDateTime.now());

        return mapToDto(productRepository.save(product));
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public void toggleProductStatus(String id, boolean active) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setActive(active);
        productRepository.save(product);
    }

    private ProductResponseDto mapToDto(Product product) {

        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setActive(product.isActive());

        return dto;
    }
}