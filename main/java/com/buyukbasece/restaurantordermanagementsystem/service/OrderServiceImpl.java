package com.buyukbasece.restaurantordermanagementsystem.service;

import com.buyukbasece.restaurantordermanagementsystem.dto.OrderItemDto;
import com.buyukbasece.restaurantordermanagementsystem.dto.OrderRequest;
import com.buyukbasece.restaurantordermanagementsystem.entity.Order;
import com.buyukbasece.restaurantordermanagementsystem.entity.OrderItem;
import com.buyukbasece.restaurantordermanagementsystem.entity.OrderStatus;
import com.buyukbasece.restaurantordermanagementsystem.entity.Product;
import com.buyukbasece.restaurantordermanagementsystem.repository.OrderItemRepository;
import com.buyukbasece.restaurantordermanagementsystem.repository.OrderRepository;
import com.buyukbasece.restaurantordermanagementsystem.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private static final MathContext MC = new MathContext(2, RoundingMode.HALF_UP);

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional
    public Order createOrder(OrderRequest orderRequest) {
        Optional<Order> existingOrder = orderRepository.findByTableNumberAndStatus(orderRequest.getTableNumber(), OrderStatus.PENDING);
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            for (OrderItemDto itemRequest : orderRequest.getItems()) {
                addOrderItem(order.getId(), itemRequest.getProductId(), itemRequest.getQuantity());
            }
            return order;
        }

        Order order = new Order();
        order.setTableNumber(orderRequest.getTableNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(BigDecimal.ZERO);

        orderRequest.getItems().forEach(itemDto -> {
            addOrderItemToOrder(order, itemDto.getProductId(), itemDto.getQuantity());
        });

        return orderRepository.save(order);
    }

    // createOrder ve addOrderItemda tekrar eden kodu tek bir metoda taşıdım
    private void addOrderItemToOrder(Order order, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("ürün bulunamadı: " + productId));

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(product.getPrice());

        BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(quantity), MC);
        order.setTotalAmount(order.getTotalAmount().add(itemTotal));
        order.addOrderItem(orderItem);
    }


    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sipariş bulunamadı. ID: " + id));
    }

    @Override
    public Optional<Order> getOrderByTableNumber(String tableNumber) {
        return orderRepository.findByTableNumberAndStatus(tableNumber, OrderStatus.PENDING);
    }

    @Override
    @Transactional
    public Order addOrderItem(Long orderId, Long productId, int quantity) {
        Order order = getOrderById(orderId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Ürün bulunamadı. ID: " + productId));

        Optional<OrderItem> existingItemOpt = order.getOrderItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            OrderItem item = existingItemOpt.get();
            item.setQuantity(item.getQuantity() + quantity);
            orderItemRepository.save(item);
        } else {
            OrderItem newItem = new OrderItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPrice());
            order.addOrderItem(newItem);
        }

        recalculateOrderTotal(order);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order removeOrderItem(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new EntityNotFoundException("sipariş öğesi bulunamadı. id: " + orderItemId));
        Order order = orderItem.getOrder();
        order.removeOrderItem(orderItem);
        orderItemRepository.delete(orderItem);

        recalculateOrderTotal(order);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrderItemQuantity(Long orderItemId, int quantity) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new EntityNotFoundException("sipariş öğesi bulunamadı. id: " + orderItemId));

        if (quantity <= 0) {
            return removeOrderItem(orderItemId);
        }

        orderItem.setQuantity(quantity);
        orderItemRepository.save(orderItem);
        Order order = orderItem.getOrder();

        recalculateOrderTotal(order);
        return orderRepository.save(order);
    }

    private void recalculateOrderTotal(Order order) {
        BigDecimal total = order.getOrderItems().stream()
                .map(item -> item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()), MC))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
    }

    @Override
    @Transactional
    public Order completeOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Sipariş zaten tamamlanmış durumda. ID: " + orderId);
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.COMPLETED);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Silinecek sipariş bulunamadı. ID: " + id);
        }
        orderRepository.deleteById(id);
    }

    @Override
    public BigDecimal getDailyTotalAmount(LocalDate date) {
        BigDecimal result = orderRepository.getDailyTotalAmount(date);
        return result != null ? result : BigDecimal.ZERO;    }


    @Override
    public Long getDailyCustomerCount(LocalDate date) {
        Long result = orderRepository.getDailyCustomerCount(date);
        return result != null ? result : 0L;    }

}