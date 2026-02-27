package com.buyukbasece.restaurantordermanagementsystem.controller;

import com.buyukbasece.restaurantordermanagementsystem.dto.OrderRequest;
import com.buyukbasece.restaurantordermanagementsystem.entity.Order;
import com.buyukbasece.restaurantordermanagementsystem.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest) {
        Order createdOrder = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @PutMapping("/complete/{orderId}")
    public ResponseEntity<Order> completeOrder(@PathVariable Long orderId) {
        Order completedOrder = orderService.completeOrder(orderId);
        return ResponseEntity.ok(completedOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/table/{tableNumber}")
    public ResponseEntity<Order> getOrderByTableNumber(@PathVariable String tableNumber) {
        Optional<Order> orderOptional = orderService.getOrderByTableNumber(tableNumber);
        if (orderOptional.isPresent()) {
            return ResponseEntity.ok(orderOptional.get());
        } else {
            Order emptyOrder = new Order();
            emptyOrder.setTableNumber(tableNumber);
            emptyOrder.setOrderItems(new ArrayList<>());
            return ResponseEntity.ok(emptyOrder);        }
    }

    @GetMapping("/summary/{date}")
    public ResponseEntity<Map<String, Object>> getDailySummary(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date); // frontend’den yyyy-MM-dd geliyor
        BigDecimal ciro = orderService.getDailyTotalAmount(localDate);
        Long musteriSayisi = orderService.getDailyCustomerCount(localDate);

        Map<String, Object> summary = new HashMap<>();
        summary.put("ciro", ciro != null ? ciro : BigDecimal.ZERO);
        summary.put("musteriSayisi", musteriSayisi);

        return ResponseEntity.ok(summary);
    }

}