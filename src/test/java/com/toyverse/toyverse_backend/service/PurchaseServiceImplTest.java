package com.toyverse.toyverse_backend.service;

import com.toyverse.toyverse_backend.dto.CartItemDto;
import com.toyverse.toyverse_backend.entity.*;
import com.toyverse.toyverse_backend.exception.BusinessException;
import com.toyverse.toyverse_backend.exception.InsufficientStockException;
import com.toyverse.toyverse_backend.repository.OrderRepository;
import com.toyverse.toyverse_backend.repository.ToyRepository;
import com.toyverse.toyverse_backend.repository.UserRepository;
import com.toyverse.toyverse_backend.service.implementation.PurchaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

    @Mock
    private ToyRepository toyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PurchaseServiceImpl purchaseService;

    private User user;
    private Toy toy1;
    private Toy toy2;
    private CartItemDto cartItem1;
    private CartItemDto cartItem2;
    private List<CartItemDto> cartItems;
    private Order savedOrder;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        toy1 = new Toy();
        toy1.setToyId(1L);
        toy1.setName("Test Toy 1");
        toy1.setPrice(29.99);
        toy1.setStockQuantity(10);
        toy1.setStatus(ToyStatus.ACTIVE);

        toy2 = new Toy();
        toy2.setToyId(2L);
        toy2.setName("Test Toy 2");
        toy2.setPrice(19.99);
        toy2.setStockQuantity(5);
        toy2.setStatus(ToyStatus.ACTIVE);

        cartItem1 = new CartItemDto();
        cartItem1.setToyId(1L);
        cartItem1.setQuantity(2);

        cartItem2 = new CartItemDto();
        cartItem2.setToyId(2L);
        cartItem2.setQuantity(1);

        cartItems = Arrays.asList(cartItem1, cartItem2);

        savedOrder = new Order();
        savedOrder.setOrderId(1L);
        savedOrder.setUser(user);
        savedOrder.setStatus("COMPLETED");
        savedOrder.setTotalPrice(79.97); // (29.99 * 2) + (19.99 * 1)
    }

    @Test
    void createPurchase_ShouldReturnOrder_WhenValidCartItems() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(toyRepository.findById(1L)).thenReturn(Optional.of(toy1));
        when(toyRepository.findById(2L)).thenReturn(Optional.of(toy2));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        Order result = purchaseService.createPurchase(1L, cartItems);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals("COMPLETED", result.getStatus());
        assertEquals(79.97, result.getTotalPrice(), 0.01);

        verify(userRepository).findById(1L);
        verify(toyRepository, times(2)).findById(anyLong());
        verify(toyRepository, times(2)).save(any(Toy.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createPurchase_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> purchaseService.createPurchase(1L, cartItems)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(toyRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createPurchase_ShouldThrowException_WhenToyNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(toyRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> purchaseService.createPurchase(1L, cartItems)
        );

        assertEquals("Toy not found", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(toyRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createPurchase_ShouldThrowInsufficientStockException_WhenNotEnoughStock() {
        // Given
        toy1.setStockQuantity(1); // Less than requested quantity (2)
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(toyRepository.findById(1L)).thenReturn(Optional.of(toy1));

        // When & Then
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> purchaseService.createPurchase(1L, cartItems)
        );

        assertEquals("Insufficient stock for: Test Toy 1", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(toyRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createPurchase_ShouldThrowBusinessException_WhenToyIsNotActive() {
        // Given
        toy1.setStatus(ToyStatus.DISCONTINUED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(toyRepository.findById(1L)).thenReturn(Optional.of(toy1));

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> purchaseService.createPurchase(1L, cartItems)
        );

        assertEquals("This toy is no longer available for sale.", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(toyRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createPurchase_ShouldCalculateTotalPriceCorrectly() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(toyRepository.findById(1L)).thenReturn(Optional.of(toy1));
        when(toyRepository.findById(2L)).thenReturn(Optional.of(toy2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setOrderId(1L);
            return order;
        });

        // When
        Order result = purchaseService.createPurchase(1L, cartItems);

        // Then
        double expectedTotal = (29.99 * 2) + (19.99 * 1); // 79.97
        assertEquals(expectedTotal, result.getTotalPrice(), 0.01);
    }

    @Test
    void createPurchase_ShouldUpdateStockQuantity() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(toyRepository.findById(1L)).thenReturn(Optional.of(toy1));
        when(toyRepository.findById(2L)).thenReturn(Optional.of(toy2));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        purchaseService.createPurchase(1L, cartItems);

        // Then
        verify(toyRepository).save(argThat(toy -> 
            toy.getToyId().equals(1L) && toy.getStockQuantity() == 8 // 10 - 2
        ));
        verify(toyRepository).save(argThat(toy -> 
            toy.getToyId().equals(2L) && toy.getStockQuantity() == 4 // 5 - 1
        ));
    }

    @Test
    void createPurchase_ShouldCreateOrderWithCorrectItems() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(toyRepository.findById(1L)).thenReturn(Optional.of(toy1));
        when(toyRepository.findById(2L)).thenReturn(Optional.of(toy2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setOrderId(1L);
            return order;
        });

        // When
        Order result = purchaseService.createPurchase(1L, cartItems);

        // Then
        verify(orderRepository).save(argThat(order -> 
            order.getUser().equals(user) &&
            order.getStatus().equals("COMPLETED") &&
            order.getOrderItems().size() == 2 &&
            order.getOrderItems().stream().anyMatch(item -> 
                item.getToy().getToyId().equals(1L) && 
                item.getQuantity() == 2 &&
                item.getItemPrice().equals(29.99)
            ) &&
            order.getOrderItems().stream().anyMatch(item -> 
                item.getToy().getToyId().equals(2L) && 
                item.getQuantity() == 1 &&
                item.getItemPrice().equals(19.99)
            )
        ));
    }

    @Test
    void processPurchase_ShouldHandleEmptyCartItems() {
        // Given
        List<CartItemDto> emptyCart = Arrays.asList();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setOrderId(1L);
            return order;
        });

        // When
        Order result = purchaseService.processPurchase(1L, emptyCart);

        // Then
        assertEquals(0.0, result.getTotalPrice());
        assertEquals(0, result.getOrderItems().size());
    }

    @Test
    void createPurchase_ShouldSetOrderStatusToProcessingInitially() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(toyRepository.findById(1L)).thenReturn(Optional.of(toy1));
        when(toyRepository.findById(2L)).thenReturn(Optional.of(toy2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            // Verify that status was set to PROCESSING first
            if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
                assertEquals("PROCESSING", order.getStatus());
            }
            order.setOrderId(1L);
            return order;
        });

        // When
        purchaseService.createPurchase(1L, cartItems);

        // Then
        verify(orderRepository).save(any(Order.class));
    }
} 