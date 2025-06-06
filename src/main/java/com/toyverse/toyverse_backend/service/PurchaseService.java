package com.toyverse.toyverse_backend.service;

import com.toyverse.toyverse_backend.dto.CartItem;
import com.toyverse.toyverse_backend.entity.Order;

import java.util.List;

public interface PurchaseService {
    Order processPurchase(Long userId, List<CartItem> cartItems);
}
