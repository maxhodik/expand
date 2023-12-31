package com.example.hodik.expand.controller;

import com.example.hodik.expand.controller.dto.ProductDto;
import com.example.hodik.expand.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping("/add")
    public ResponseEntity<?> createProduct(@RequestBody @Valid ProductDto productDto) {

        return ResponseEntity.ok(productService.createProduct(productDto));
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllProducts() {
        return ResponseEntity.ok(productService.findAll());
    }
}
