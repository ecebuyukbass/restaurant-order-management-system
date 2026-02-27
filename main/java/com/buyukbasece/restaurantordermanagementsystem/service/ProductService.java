package com.buyukbasece.restaurantordermanagementsystem.service;

import com.buyukbasece.restaurantordermanagementsystem.entity.Product;

import java.util.List;

public interface ProductService {


    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product saveProduct(Product product);
    void deleteProduct(Long id);
}
