package com.cottin.productservice.service;

import com.cottin.productservice.dto.ProductRequestDto;
import com.cottin.productservice.dto.ProductResponseDto;

import java.util.List;

public interface ProductService {

    void createProduct(ProductRequestDto productRequest);

    List<ProductResponseDto> getAllProducts();
}
