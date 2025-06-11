package com.toyverse.toyverse_backend.controller;

import com.toyverse.toyverse_backend.dto.CartItemDto;
import com.toyverse.toyverse_backend.dto.OrderDto;
import com.toyverse.toyverse_backend.dto.mapper.OrderMapper;
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
    public ResponseEntity<?> createPurchase(@Valid @RequestBody List<CartItemDto> cartItems) {
        try {
            Long userId = securityUtils.getCurrentUser().getId();
            Order order = purchaseService.createPurchase(userId, cartItems);
            OrderDto orderDto = OrderMapper.toDto(order);
            return ResponseEntity.ok(orderDto);
        } catch (InsufficientStockException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (PaymentFailedException e) {
            return ResponseEntity.status(402).body(e.getMessage());
        }
    }

}
