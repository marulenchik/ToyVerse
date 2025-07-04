package com.toyverse.toyverse_backend.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CartItemDto {
    private Long toyId;

    @Min(1)
    private Integer quantity;
}