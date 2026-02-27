package com.buyukbasece.restaurantordermanagementsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String tableNumber;
    private List<OrderItemDto> items;
}
