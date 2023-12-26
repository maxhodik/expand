package com.example.hodik.expand.service.impl;

import com.example.hodik.expand.controller.dto.ProductDto;
import com.example.hodik.expand.controller.dto.RecordDto;
import com.example.hodik.expand.model.Products;
import com.example.hodik.expand.repository.ProductRepository;
import com.example.hodik.expand.service.ProductService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {


    public final EntityManager entityManager;
    public final ProductRepository productRepository;

    public ProductServiceImpl(EntityManager entityManager, ProductRepository productRepository) {

        this.entityManager = entityManager;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Object createProduct(ProductDto productDto) {
        String tableName = productDto.getTable();
        entityManager.createNativeQuery("CREATE TABLE IF NOT EXISTS " + tableName + " (id INT AUTO_INCREMENT PRIMARY KEY, " + "entry_date DATE, item_code INT, " +
                "item_name VARCHAR(255), item_quantity INT, status VARCHAR(255))").executeUpdate();

        List<RecordDto> records = productDto.getRecords();
        for (RecordDto rowDto : records) {
            entityManager.createNativeQuery("INSERT INTO " + tableName + "(entry_date, item_code, item_name, item_quantity, status)" +
                            " VALUES (?,?,?,?,?)")
                    .setParameter(1, rowDto.getEntryDate())
                    .setParameter(2, rowDto.getItemCode())
                    .setParameter(3, rowDto.getItemName())
                    .setParameter(4, rowDto.getItemQuantity())
                    .setParameter(5, rowDto.getStatus()).executeUpdate();
        }
        return records;
    }

    @Override
    public List<Products> findAll() {
        return productRepository.findAll();
    }
}
