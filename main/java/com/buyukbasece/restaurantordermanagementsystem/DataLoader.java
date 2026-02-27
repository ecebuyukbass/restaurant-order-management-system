package com.buyukbasece.restaurantordermanagementsystem;

import com.buyukbasece.restaurantordermanagementsystem.entity.Product;
import com.buyukbasece.restaurantordermanagementsystem.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    private final ProductRepository productRepository;

    public DataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            productRepository.saveAll(List.of(
                    new Product("Alfredo soslu makarna", 250),
                    new Product("Bolonez soslu makarna", 250),
                    new Product("Pesto soslu makarna", 240),
                    new Product("Napoliten soslu makarna", 240),
                    new Product("Limonata", 70),
                    new Product("Coca Cola", 50),
                    new Product("Ayran", 30),
                    new Product("Soda", 40)
            ));
        }
    }
}
