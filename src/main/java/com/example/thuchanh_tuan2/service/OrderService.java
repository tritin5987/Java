package com.example.thuchanh_tuan2.service;

import com.example.thuchanh_tuan2.model.CartItem;
import com.example.thuchanh_tuan2.model.Order;
import com.example.thuchanh_tuan2.model.OrderDetail;
import com.example.thuchanh_tuan2.repository.OrderDetailRepository;
import com.example.thuchanh_tuan2.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartService cartService;

    public Order createOrder(String customerName,
                             String shippingAddress,
                             String phoneNumber,
                             String email,
                             String notes,
                             String paymentMethod,
                             List<CartItem> cartItems) {

        // Create new Order
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setNotes(notes);
        order.setPaymentMethod(paymentMethod);
        order = orderRepository.save(order);

        // Create OrderDetails for each CartItem
        for (CartItem cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetailRepository.save(orderDetail);
        }

        // Clear the cart after order creation
        cartService.clearCart();

        return order;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }
    public Order updateOrder(Order updatedOrder) {
        Order existingOrder = getOrderById(updatedOrder.getId());
        existingOrder.setCustomerName(updatedOrder.getCustomerName());
        existingOrder.setShippingAddress(updatedOrder.getShippingAddress());
        existingOrder.setEmail(updatedOrder.getEmail());
        existingOrder.setNotes(updatedOrder.getNotes());
        existingOrder.setPaymentMethod(updatedOrder.getPaymentMethod());
        // Add more fields as needed

        return orderRepository.save(existingOrder);
    }
    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
