package com.example.thuchanh_tuan2.controller;

import com.example.thuchanh_tuan2.model.CartItem;
import com.example.thuchanh_tuan2.model.Order;
import com.example.thuchanh_tuan2.model.OrderDetail;
import com.example.thuchanh_tuan2.service.CartService;
import com.example.thuchanh_tuan2.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/checkout")
    public String checkout() {
        return "/cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(String customerName,
                              String shippingAddress,
                              String phoneNumber,
                              String email,
                              String notes,
                              String paymentMethod) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; // Redirect if cart is empty
        }
        orderService.createOrder(customerName, shippingAddress, phoneNumber, email, notes, paymentMethod, cartItems);
        return "redirect:/order/confirmation";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Your order has been successfully placed.");
        return "cart/order-confirmation";
    }

    @PostMapping("/updateQuantity")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCartItemQuantity(@RequestParam Long productId, @RequestParam int quantity) {
        cartService.updateCartItemQuantity(productId, quantity);
        double totalPrice = cartService.getTotalPrice();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalPrice", totalPrice);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "order/order-list";
    }

    @PostMapping("/{orderId}/delete")
    public String deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return "redirect:/order/list";
    }

    @GetMapping("/{orderId}/edit")
    public String showEditOrderForm(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        model.addAttribute("order", order);
        return "order/order-edit";
    }

    @PostMapping("/{orderId}/edit")
    public String updateOrder(@PathVariable Long orderId,
                              @RequestParam String customerName,
                              @RequestParam String shippingAddress,
                              @RequestParam String email,
                              @RequestParam String notes,
                              @RequestParam String paymentMethod) {
        Order order = orderService.getOrderById(orderId);
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setEmail(email);
        order.setNotes(notes);
        order.setPaymentMethod(paymentMethod);

        orderService.updateOrder(order);

        return "redirect:/order/list";
    }

    @GetMapping("/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        List<OrderDetail> orderDetails = orderService.getOrderDetailsByOrderId(orderId);

        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        return "order/order-detail";
    }
}
