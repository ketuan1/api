package com.project.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class BasketDto {
    private Long id;

    private Long user_id;

    private List<BasketItemDto> basketItem;
}
