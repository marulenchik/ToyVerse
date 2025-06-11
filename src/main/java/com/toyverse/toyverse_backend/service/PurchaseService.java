package com.toyverse.toyverse_backend.service;

import com.toyverse.toyverse_backend.dto.CartItemDto;
import com.toyverse.toyverse_backend.entity.Order;

import java.util.List;

public interface PurchaseService {
    Order createPurchase(Long userId, List<CartItemDto> cartItems) ;
    Order processPurchase(Long userId, List<CartItemDto> cartItems);
}
