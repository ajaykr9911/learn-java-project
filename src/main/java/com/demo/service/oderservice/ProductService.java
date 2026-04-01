package com.demo.service.oderservice;

import com.demo.model.Product;
import com.demo.model.dto.CreateProductRequest;
import com.demo.model.dto.ProductResponseDto;
import com.demo.model.dto.SearchProductDto;
import com.demo.model.dto.VariantDto;
import com.demo.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;

    public ProductResponseDto createProduct(CreateProductRequest request, MultipartFile image) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setBrand(request.getBrand());
        product.setCategory(request.getCategory());
        product.setSubCategory(request.getSubCategory());
        product.setStock(request.getStock());
        product.setSku(request.getSku());
        product.setAttributes(request.getAttributes());

        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());

        if (image != null && !image.isEmpty()) {
            product.setImages(saveFile(image));
        }

        Product saved = productRepository.save(product);

        return mapToDto(saved);
    }

    private String saveFile(MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" +
                    Objects.requireNonNull(file.getOriginalFilename()).replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            Path path = Paths.get("uploads/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            return "http://localhost:8080/uploads/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("File upload failed");
        }
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
        product.setDiscountPrice(request.getDiscountPrice());
        product.setBrand(request.getBrand());
        product.setCategory(request.getCategory());
        product.setSubCategory(request.getSubCategory());
        product.setStock(request.getStock());
        product.setSku(request.getSku());
        product.setAttributes(request.getAttributes());

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
        dto.setBrand(product.getBrand());

        dto.setPrice(product.getPrice());
        dto.setDiscountPrice(product.getDiscountPrice());

        dto.setCategory(product.getCategory());
        dto.setSubCategory(product.getSubCategory());

        dto.setImages(product.getImages());

        dto.setRating(product.getRating());
        dto.setReviewCount(product.getReviewCount());

        dto.setStock(product.getStock());
        dto.setInStock(product.getStock() > 0);

        dto.setAttributes(product.getAttributes());

        dto.setActive(product.isActive());

        return dto;
    }
    public Page<ProductResponseDto> searchProducts(SearchProductDto dto) {

        Query query = new Query();

        // 🔍 Keyword search
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("name").regex(dto.getKeyword(), "i"),
                    Criteria.where("description").regex(dto.getKeyword(), "i")
            ));
        }

        // 📂 Category filter
        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            query.addCriteria(Criteria.where("category").in(dto.getCategories()));
        }

        // 🏷 Brand filter
        if (dto.getBrands() != null && !dto.getBrands().isEmpty()) {
            query.addCriteria(Criteria.where("brand").in(dto.getBrands()));
        }

        // 💰 Price range
        if (dto.getMinPrice() != null || dto.getMaxPrice() != null) {
            Criteria priceCriteria = Criteria.where("price");

            if (dto.getMinPrice() != null) priceCriteria.gte(dto.getMinPrice());
            if (dto.getMaxPrice() != null) priceCriteria.lte(dto.getMaxPrice());

            query.addCriteria(priceCriteria);
        }

        // 📦 Stock filter
        if (Boolean.TRUE.equals(dto.getInStock())) {
            query.addCriteria(Criteria.where("stock").gt(0));
        }

        // 🔥 Active filter
        if (dto.getActive() != null) {
            query.addCriteria(Criteria.where("active").is(dto.getActive()));
        }

        // 🔽 Sorting
        Sort sort = Sort.by(
                dto.getSortDir().equalsIgnoreCase("asc") ?
                        Sort.Direction.ASC : Sort.Direction.DESC,
                dto.getSortBy()
        );

        // 📄 Pagination
        Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize(), sort);
        query.with(pageable);

        // ✅ Get total count (IMPORTANT)
        long total = mongoTemplate.count(query.skip(0).limit(0), Product.class);

        // ✅ Get paginated data
        List<Product> products = mongoTemplate.find(query, Product.class);

        List<ProductResponseDto> content = products.stream()
                .map(this::mapToDto)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }
}