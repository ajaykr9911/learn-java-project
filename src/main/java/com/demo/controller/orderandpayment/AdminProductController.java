package com.demo.controller.orderandpayment;

import com.demo.model.dto.CreateProductRequest;
import com.demo.model.dto.ProductResponseDto;
import com.demo.model.dto.SearchProductDto;
import com.demo.service.oderservice.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    // ✅ Create Product
    @PostMapping(consumes = "multipart/form-data")
    public ProductResponseDto create(
            @RequestPart("data") CreateProductRequest request,
            @RequestPart(value = "images", required = false) MultipartFile images
    ) {
        return productService.createProduct(request, images);
    }

    // ✅ Get All Products (Pagination)
    @GetMapping
    public Page<ProductResponseDto> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.getAllProducts(page, size);
    }

    // ✅ Update Product
    @PutMapping("/{id}")
    public ProductResponseDto update(
            @PathVariable String id,
            @RequestBody CreateProductRequest request
    ) {
        return productService.updateProduct(id, request);
    }

    // ✅ Delete Product
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        productService.deleteProduct(id);
    }

    // ✅ Activate / Deactivate Product
    @PatchMapping("/{id}/status")
    public void toggleStatus(
            @PathVariable String id,
            @RequestParam boolean active
    ) {
        productService.toggleProductStatus(id, active);
    }

    @PostMapping("/search-product")
    public Page<ProductResponseDto> searchProducts(@RequestBody SearchProductDto query) {
        return productService.searchProducts(query);
    }

}