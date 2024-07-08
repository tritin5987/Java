package com.example.thuchanh_tuan2.model;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
