package com.toyverse.toyverse_backend.service.implementation;

import com.toyverse.toyverse_backend.dto.CartItemDto;
import com.toyverse.toyverse_backend.entity.*;
import com.toyverse.toyverse_backend.exception.InsufficientStockException;
import com.toyverse.toyverse_backend.repository.OrderRepository;
import com.toyverse.toyverse_backend.repository.ToyRepository;
import com.toyverse.toyverse_backend.repository.UserRepository;
import com.toyverse.toyverse_backend.security.SecurityUtils;
import com.toyverse.toyverse_backend.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {
    private final ToyRepository toyRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final SecurityUtils securityUtils;

    @Override
    public Order createPurchase(Long userId, List<CartItemDto> cartItems) {
        return processPurchase(userId, cartItems);
    }


    @Override
    public Order processPurchase(Long userId, List<CartItemDto> cartItems) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PROCESSING");
        order.setCreatedAt(new Date());

        double totalPrice = 0;
        for (CartItemDto item : cartItems) {
            Toy toy = toyRepository.findById(item.getToyId())
                    .orElseThrow(() -> new RuntimeException("Toy not found"));

            if (toy.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for: " + toy.getName()
                );
            }
            totalPrice += toy.getPrice() * item.getQuantity();
        }
        order.setTotalPrice(totalPrice);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemDto item : cartItems) {
            Toy toy = toyRepository.findById(item.getToyId()).get();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setToy(toy);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setItemPrice(toy.getPrice());
            orderItems.add(orderItem);

            toy.setStockQuantity(toy.getStockQuantity() - item.getQuantity());
            toyRepository.save(toy);
        }

        order.setOrderItems(orderItems);
        order.setStatus("COMPLETED");
        return orderRepository.save(order);
    }
}


