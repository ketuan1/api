package com.project.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class BasketDto {
    private Long id;

    private String email;

    private List<BasketItemDto> basketItem;
}
