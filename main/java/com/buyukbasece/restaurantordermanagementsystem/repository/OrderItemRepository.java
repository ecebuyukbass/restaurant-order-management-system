package com.buyukbasece.restaurantordermanagementsystem.repository;

import com.buyukbasece.restaurantordermanagementsystem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
