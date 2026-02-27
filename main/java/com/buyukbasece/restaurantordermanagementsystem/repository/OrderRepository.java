package com.buyukbasece.restaurantordermanagementsystem.repository;

import com.buyukbasece.restaurantordermanagementsystem.entity.Order;
import com.buyukbasece.restaurantordermanagementsystem.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    List<Order> findByTableNumber(String tableNumber);

    Optional<Order> findByTableNumberAndStatus(String tableNumber, OrderStatus status);

    List<Order> findByTableNumberAndStatusNot(String tableNumber, String status);
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate = :orderDate")
    BigDecimal getDailyTotalAmount(@Param("orderDate") LocalDate orderDate);

    @Query("SELECT COUNT(DISTINCT o.tableNumber) FROM Order o WHERE o.orderDate = :orderDate")
    Long getDailyCustomerCount(@Param("orderDate") LocalDate orderDate);

}