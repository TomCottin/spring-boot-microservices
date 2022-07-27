package com.cottin.productservice.service;

import com.cottin.productservice.dto.ProductRequestDto;
import com.cottin.productservice.dto.ProductResponseDto;
import com.cottin.productservice.model.Product;
import com.cottin.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public void createProduct(ProductRequestDto productRequest) {
        // create product
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        // save product
        productRepository.save(product);
        // log info
        log.info("Product created: {}", product.getId());
    }

    public List<ProductResponseDto> getAllProducts() {
        // get all products
        List<Product> products = productRepository.findAll();
        // log info
        log.info("All products retrieved");
        // convert to response
        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponseDto mapToProductResponse(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
