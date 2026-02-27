package com.buyukbasece.restaurantordermanagementsystem.service;

import com.buyukbasece.restaurantordermanagementsystem.dto.OrderRequest;
import com.buyukbasece.restaurantordermanagementsystem.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(OrderRequest orderRequest);
    List<Order> getAllOrders();
    Order getOrderById(Long id);

    Optional<Order> getOrderByTableNumber(String tableNumber);
    BigDecimal getDailyTotalAmount(LocalDate date);
    Long getDailyCustomerCount(LocalDate date);

    Order addOrderItem(Long orderId, Long productId, int quantity);
    Order removeOrderItem(Long orderItemId);
    Order updateOrderItemQuantity(Long orderItemId, int quantity);
    Order completeOrder(Long orderId);
    void deleteOrder(Long id);
}