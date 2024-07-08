package com.example.thuchanh_tuan2.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String description;
    private String imageUrl; // Thêm trường này để lưu trữ URL hình ảnh
    @Transient // Đánh dấu thuộc tính này không được lưu vào database
    private MultipartFile file;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
