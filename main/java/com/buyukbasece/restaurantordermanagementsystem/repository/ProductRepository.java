package com.buyukbasece.restaurantordermanagementsystem.repository;

import com.buyukbasece.restaurantordermanagementsystem.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
