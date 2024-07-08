package com.example.thuchanh_tuan2.repository;

import com.example.thuchanh_tuan2.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContaining(String name);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product> findByNameContainingAndPriceBetween(String name, Double minPrice, Double maxPrice);
}
