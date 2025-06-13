package com.toyverse.toyverse_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyverse.toyverse_backend.dto.ReviewRequestDto;
import com.toyverse.toyverse_backend.dto.ReviewResponseDto;
import com.toyverse.toyverse_backend.entity.User;
import com.toyverse.toyverse_backend.security.SecurityUtils;
import com.toyverse.toyverse_backend.service.ReviewService;
import com.toyverse.toyverse_backend.service.ToyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private ToyService toyService;

    @MockitoBean
    private SecurityUtils securityUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private ReviewRequestDto reviewRequest;
    private ReviewResponseDto reviewResponse;
    private User mockUser;

    @BeforeEach
    void setUp() {
        reviewRequest = new ReviewRequestDto();
        reviewRequest.setRating(5);
        reviewRequest.setComment("Great toy!");

        reviewResponse = new ReviewResponseDto();
        reviewResponse.setReviewId(1L);
        reviewResponse.setRating(5);
        reviewResponse.setComment("Great toy!");
        reviewResponse.setUsername("testuser");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
    }

    @Test
    void createReview_ShouldReturnReviewResponse_WhenValidRequest() throws Exception {
        // Given
        Long toyId = 1L;
        when(toyService.getToyById(toyId)).thenReturn(new com.toyverse.toyverse_backend.entity.Toy());
        when(securityUtils.getCurrentUser()).thenReturn(mockUser);
        when(reviewService.createReview(eq(mockUser), eq(toyId), any(ReviewRequestDto.class)))
                .thenReturn(reviewResponse);

        // When & Then
        mockMvc.perform(post("/api/toys/{toyId}/reviews", toyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1L))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("Great toy!"))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(toyService).getToyById(toyId);
        verify(securityUtils).getCurrentUser();
        verify(reviewService).createReview(eq(mockUser), eq(toyId), any(ReviewRequestDto.class));
    }

    @Test
    void createReview_ShouldReturnNotFound_WhenToyDoesNotExist() throws Exception {
        // Given
        Long toyId = 999L;
        when(toyService.getToyById(toyId)).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/toys/{toyId}/reviews", toyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isNotFound());

        verify(toyService).getToyById(toyId);
        verify(securityUtils, never()).getCurrentUser();
        verify(reviewService, never()).createReview(any(), any(), any());
    }

    @Test
    void createReview_ShouldReturnBadRequest_WhenInvalidRating() throws Exception {
        // Given
        Long toyId = 1L;
        ReviewRequestDto invalidRequest = new ReviewRequestDto();
        invalidRequest.setRating(0); // Invalid rating (should be 1-5)
        invalidRequest.setComment("Test comment");

        // When & Then
        mockMvc.perform(post("/api/toys/{toyId}/reviews", toyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReview_ShouldReturnBadRequest_WhenEmptyComment() throws Exception {
        // Given
        Long toyId = 1L;
        ReviewRequestDto invalidRequest = new ReviewRequestDto();
        invalidRequest.setRating(5);
        invalidRequest.setComment(""); // Empty comment

        // When & Then
        mockMvc.perform(post("/api/toys/{toyId}/reviews", toyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getReviews_ShouldReturnReviewsList_WhenToyExists() throws Exception {
        // Given
        Long toyId = 1L;
        ReviewResponseDto review1 = new ReviewResponseDto();
        review1.setReviewId(1L);
        review1.setRating(5);
        review1.setComment("Great toy!");
        review1.setUsername("user1");

        ReviewResponseDto review2 = new ReviewResponseDto();
        review2.setReviewId(2L);
        review2.setRating(4);
        review2.setComment("Good quality");
        review2.setUsername("user2");

        List<ReviewResponseDto> reviews = Arrays.asList(review1, review2);
        when(reviewService.getReviewsByToyId(toyId)).thenReturn(reviews);

        // When & Then
        mockMvc.perform(get("/api/toys/{toyId}/reviews", toyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].reviewId").value(1L))
                .andExpect(jsonPath("$[0].rating").value(5))
                .andExpect(jsonPath("$[0].comment").value("Great toy!"))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].reviewId").value(2L))
                .andExpect(jsonPath("$[1].rating").value(4))
                .andExpect(jsonPath("$[1].comment").value("Good quality"))
                .andExpect(jsonPath("$[1].username").value("user2"));

        verify(reviewService).getReviewsByToyId(toyId);
    }

    @Test
    void getReviews_ShouldReturnEmptyList_WhenNoReviewsExist() throws Exception {
        // Given
        Long toyId = 1L;
        when(reviewService.getReviewsByToyId(toyId)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/toys/{toyId}/reviews", toyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(reviewService).getReviewsByToyId(toyId);
    }
} 