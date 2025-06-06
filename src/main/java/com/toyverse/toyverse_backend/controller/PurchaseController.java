package com.toyverse.toyverse_backend.controller;

import com.toyverse.toyverse_backend.dto.CartItem;
import com.toyverse.toyverse_backend.dto.OrderDto;
import com.toyverse.toyverse_backend.dto.OrderMapper;
import com.toyverse.toyverse_backend.entity.Order;
import com.toyverse.toyverse_backend.exception.InsufficientStockException;
import com.toyverse.toyverse_backend.exception.PaymentFailedException;
import com.toyverse.toyverse_backend.security.SecurityUtils;
import com.toyverse.toyverse_backend.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<?> createPurchase(@Valid @RequestBody List<CartItem> cartItems) {
        try {
            Long userId = securityUtils.getCurrentUser().getId();
            Order orderEntity = purchaseService.processPurchase(userId, cartItems);
            OrderDto orderDto = OrderMapper.toDto(orderEntity);
            return ResponseEntity.ok(orderDto);
        } catch (InsufficientStockException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (PaymentFailedException e) {
            return ResponseEntity.status(402).body(e.getMessage());
        }
    }
}
