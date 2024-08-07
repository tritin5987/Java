package com.example.thuchanh_tuan2.controller;

import com.example.thuchanh_tuan2.HandleImage;
import com.example.thuchanh_tuan2.model.Product;
import com.example.thuchanh_tuan2.service.CategoryService;
import com.example.thuchanh_tuan2.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final HandleImage handleImage;

    // Display a list of all products
    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products/product-list";
    }

    // For adding a new product
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/add-product";
    }

    // Process the form for adding a new product
    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult result,
                             @RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/add-product";
        }

        if (!file.isEmpty()) {
            String fileName = handleImage.saveImage(file);
            product.setImageUrl(fileName);
        }

        productService.addProduct(product, file);
        return "redirect:/products";
    }

    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/update-product";
    }

    // Process the form for updating a product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute("product") Product product,
                                BindingResult result, @RequestParam("file") MultipartFile file, Model model) throws IOException {
        if (result.hasErrors()) {
            product.setId(id); // set id to keep it in the form in case of errors
            model.addAttribute("categories", categoryService.getAllCategories());
            return "products/update-product";
        }

        if (!file.isEmpty()) {
            String fileName = handleImage.saveImage(file);
            product.setImageUrl(fileName);
        }

        productService.updateProduct(product, file);
        return "redirect:/products";
    }

    // Handle request to delete a product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }

    // Search functionality
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            Model model
    ) {
        List<Product> products;

        if ((name != null && !name.isEmpty()) || (minPrice != null) || (maxPrice != null)) {
            products = productService.searchProducts(name, minPrice, maxPrice);
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        return "products/product-list";
    }
}
