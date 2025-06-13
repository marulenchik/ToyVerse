package com.toyverse.toyverse_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyverse.toyverse_backend.dto.CartItemDto;
import com.toyverse.toyverse_backend.entity.Order;
import com.toyverse.toyverse_backend.entity.User;
import com.toyverse.toyverse_backend.exception.InsufficientStockException;
import com.toyverse.toyverse_backend.exception.PaymentFailedException;
import com.toyverse.toyverse_backend.security.SecurityUtils;
import com.toyverse.toyverse_backend.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PurchaseController.class)
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PurchaseService purchaseService;

    @MockitoBean
    private SecurityUtils securityUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private List<CartItemDto> cartItems;
    private User mockUser;
    private Order mockOrder;

    @BeforeEach
    void setUp() {
        CartItemDto item1 = new CartItemDto();
        item1.setToyId(1L);
        item1.setQuantity(2);

        CartItemDto item2 = new CartItemDto();
        item2.setToyId(2L);
        item2.setQuantity(1);

        cartItems = Arrays.asList(item1, item2);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        mockOrder = new Order();
        mockOrder.setOrderId(1L);
        mockOrder.setUser(mockUser);
        mockOrder.setTotalPrice(99.99);
        mockOrder.setStatus("COMPLETED");
        mockOrder.setCreatedAt(new Date());
    }

    @Test
    void createPurchase_ShouldReturnOrderDto_WhenValidRequest() throws Exception {
        // Given
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(purchaseService.createPurchase(eq(1L), anyList())).thenReturn(mockOrder);

        // When & Then
        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItems)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.totalPrice").value(99.99))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(securityUtils).getCurrentUser();
        verify(purchaseService).createPurchase(eq(1L), anyList());
    }

    @Test
    void createPurchase_ShouldReturnBadRequest_WhenInsufficientStock() throws Exception {
        // Given
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(purchaseService.createPurchase(eq(1L), anyList()))
                .thenThrow(new InsufficientStockException("Insufficient stock for: Test Toy"));

        // When & Then
        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItems)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient stock for: Test Toy"));

        verify(securityUtils).getCurrentUser();
        verify(purchaseService).createPurchase(eq(1L), anyList());
    }

    @Test
    void createPurchase_ShouldReturnPaymentRequired_WhenPaymentFails() throws Exception {
        // Given
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(purchaseService.createPurchase(eq(1L), anyList()))
                .thenThrow(new PaymentFailedException("Payment processing failed"));

        // When & Then
        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItems)))
                .andExpect(status().isPaymentRequired())
                .andExpect(content().string("Payment processing failed"));

        verify(securityUtils).getCurrentUser();
        verify(purchaseService).createPurchase(eq(1L), anyList());
    }

    @Test
    void createPurchase_ShouldReturnBadRequest_WhenEmptyCartItems() throws Exception {
        // Given
        List<CartItemDto> emptyCart = Arrays.asList();

        // When & Then
        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyCart)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPurchase_ShouldReturnBadRequest_WhenInvalidCartItemData() throws Exception {
        // Given
        CartItemDto invalidItem = new CartItemDto();
        invalidItem.setToyId(null); // Invalid - null toy ID
        invalidItem.setQuantity(-1); // Invalid - negative quantity
        List<CartItemDto> invalidCartItems = Arrays.asList(invalidItem);

        // When & Then
        mockMvc.perform(post("/api/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCartItems)))
                .andExpect(status().isBadRequest());
    }
} 