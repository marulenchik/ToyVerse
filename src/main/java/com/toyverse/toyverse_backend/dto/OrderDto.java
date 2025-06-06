package com.toyverse.toyverse_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long orderId;
    private Long userId;
    private String status;
    private Double totalPrice;
    private Date createdAt;
    private List<OrderItemDto> items;
}
