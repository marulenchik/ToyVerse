package com.toyverse.toyverse_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long toyId;
    private Integer quantity;
    private Double itemPrice;
}
