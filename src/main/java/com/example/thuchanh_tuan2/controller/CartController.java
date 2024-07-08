package com.example.thuchanh_tuan2.controller;

import com.example.thuchanh_tuan2.model.CartItem;
import com.example.thuchanh_tuan2.model.Order;
import com.example.thuchanh_tuan2.service.CartService;
import com.example.thuchanh_tuan2.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService; // Service để lưu Order vào cơ sở dữ liệu

    @GetMapping
    public String showCart(Model model) {
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        return "/cart/cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        cartService.addToCart(productId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId) {
        cartService.removeFromCart(productId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        double totalPrice = cartService.getTotalPrice();

        // Tạo đối tượng Order từ giỏ hàng và lưu vào cơ sở dữ liệu
        Order order = new Order();
        order.setCustomerName("Customer"); // Thay bằng thông tin thực tế từ session hoặc form
        order.setShippingAddress("Address"); // Thay bằng thông tin thực tế từ session hoặc form
        order.setPhoneNumber("123456789"); // Thay bằng thông tin thực tế từ session hoặc form
        order.setEmail("customer@example.com"); // Thay bằng thông tin thực tế từ session hoặc form
        order.setOrderDate(LocalDateTime.now()); // Ngày đặt hàng là ngày hiện tại
        order.setTotalprice(totalPrice); // Set tổng giá của đơn hàng

        // Lưu đơn hàng vào cơ sở dữ liệu
        Order savedOrder = orderService.saveOrder(order);

        // Có thể xử lý thêm các thao tác khác sau khi lưu đơn hàng, ví dụ như thanh toán, gửi email xác nhận, v.v.

        // Xóa giỏ hàng sau khi đã thực hiện đặt hàng thành công
        cartService.clearCart();

        model.addAttribute("order", savedOrder);
        return "/cart/checkout";
    }
}
