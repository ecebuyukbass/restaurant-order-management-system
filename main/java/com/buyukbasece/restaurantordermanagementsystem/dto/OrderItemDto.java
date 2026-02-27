package com.buyukbasece.restaurantordermanagementsystem.dto;


import lombok.Data;

@Data
public class OrderItemDto {
    private Long productId;
    private int quantity;
}
