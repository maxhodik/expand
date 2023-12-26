package com.example.hodik.expand.service;

import com.example.hodik.expand.controller.dto.ProductDto;
import com.example.hodik.expand.model.Products;

import java.util.List;

public interface ProductService {
    Object createProduct(ProductDto productDto);

    List<Products> findAll();
}
