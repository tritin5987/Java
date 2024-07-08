
package com.example.thuchanh_tuan2.service;
/*
import com.example.thuchanh_tuan2.model.Product;
import com.example.thuchanh_tuan2.repository.ProductRepository;
import jakarta.validation.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final String uploadDir = "./uploads/";
    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Retrieve a product by its id
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Add a new product to the database
    public Product addProduct(Product product) {

        return productRepository.save(product);
    }

    // Update an existing product
    public Product updateProduct(@NotNull Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        product.getId() + " does not exist."));
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setImageUrl(product.getImageUrl());
        return productRepository.save(existingProduct);
    }

    // Delete a product by its id
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }

    // Search functionality
    public List<Product> searchProducts(String name, Double minPrice, Double maxPrice) {
        if (name != null && !name.isEmpty() && minPrice != null && maxPrice != null) {
            return productRepository.findByNameContainingAndPriceBetween(name, minPrice, maxPrice);
        } else if (name != null && !name.isEmpty()) {
            return productRepository.findByNameContaining(name);
        } else if (minPrice != null && maxPrice != null) {
            return productRepository.findByPriceBetween(minPrice, maxPrice);
        } else {
            return getAllProducts();
        }
    }
    // Save file method
    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath);

        return fileName;
    }
}
*/



import com.example.thuchanh_tuan2.model.Product;
import com.example.thuchanh_tuan2.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    private final String uploadDir = "./uploads/";

    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Retrieve a product by its id
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Add a new product to the database
    public Product addProduct(Product product, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            product.setImageUrl(fileName); // Sử dụng setter cho imageUrl
        }
        return productRepository.save(product);
    }

    // Update an existing product
    public Product updateProduct(@NotNull Product product, MultipartFile file) throws IOException {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        product.getId() + " does not exist."));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());

        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            existingProduct.setImageUrl(fileName); // Sử dụng setter cho imageUrl
        }

        return productRepository.save(existingProduct);
    }

    // Delete a product by its id
    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }

    // Search functionality
    public List<Product> searchProducts(String name, Double minPrice, Double maxPrice) {
        if (name != null && !name.isEmpty() && minPrice != null && maxPrice != null) {
            return productRepository.findByNameContainingAndPriceBetween(name, minPrice, maxPrice);
        } else if (name != null && !name.isEmpty()) {
            return productRepository.findByNameContaining(name);
        } else if (minPrice != null && maxPrice != null) {
            return productRepository.findByPriceBetween(minPrice, maxPrice);
        } else {
            return getAllProducts();
        }
    }

    // Save file method
    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath);

        return fileName;
    }
}

