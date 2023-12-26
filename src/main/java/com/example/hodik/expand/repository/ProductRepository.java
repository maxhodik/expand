package com.example.hodik.expand.repository;

import com.example.hodik.expand.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Products, Long> {

}
