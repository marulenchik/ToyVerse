package com.toyverse.toyverse_backend.service.implementation;

import com.toyverse.toyverse_backend.dto.CartItem;
import com.toyverse.toyverse_backend.entity.*;
import com.toyverse.toyverse_backend.exception.InsufficientStockException;
import com.toyverse.toyverse_backend.repository.OrderRepository;
import com.toyverse.toyverse_backend.repository.ToyRepository;
import com.toyverse.toyverse_backend.repository.UserRepository;
import com.toyverse.toyverse_backend.service.PurchaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {
    private final ToyRepository toyRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public PurchaseServiceImpl(
            ToyRepository toyRepository,
            UserRepository userRepository,
            OrderRepository orderRepository
    ) {
        this.toyRepository = toyRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Order processPurchase(Long userId, List<CartItem> cartItems) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PROCESSING");
        order.setCreatedAt(new Date());

        double totalPrice = 0;
        for (CartItem item : cartItems) {
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
        for (CartItem item : cartItems) {
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


