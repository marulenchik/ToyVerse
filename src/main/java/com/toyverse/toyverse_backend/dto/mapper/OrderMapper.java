package com.toyverse.toyverse_backend.dto.mapper;

import com.toyverse.toyverse_backend.dto.OrderDto;
import com.toyverse.toyverse_backend.dto.OrderItemDto;
import com.toyverse.toyverse_backend.entity.Order;
import com.toyverse.toyverse_backend.entity.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDto toDto(Order order) {
        List<OrderItemDto> itemDtos = order.getOrderItems().stream()
                .map(OrderMapper::toOrderItemDto)
                .collect(Collectors.toList());

        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getId());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setItems(itemDtos);
        return dto;
    }

    private static OrderItemDto toOrderItemDto(OrderItem item) {
        return new OrderItemDto(
                item.getToy().getToyId(),
                item.getQuantity(),
                item.getItemPrice()
        );
    }
}
