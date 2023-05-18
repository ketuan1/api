package com.project.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BasketItemDto {
    private Long productId;

    private String name;

    private BigDecimal price;

    private String imageUrl;

    private String category;

    private int quantity;

    public BasketItemDto(Long productId, String name, BigDecimal price, String imageUrl, String category, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.quantity = quantity;
    }
}
