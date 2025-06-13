package com.toyverse.toyverse_backend.service;

import com.toyverse.toyverse_backend.dto.ReviewRequestDto;
import com.toyverse.toyverse_backend.dto.ReviewResponseDto;
import com.toyverse.toyverse_backend.entity.Review;
import com.toyverse.toyverse_backend.entity.Toy;
import com.toyverse.toyverse_backend.entity.User;
import com.toyverse.toyverse_backend.exception.NotPurchasedException;
import com.toyverse.toyverse_backend.repository.OrderItemRepository;
import com.toyverse.toyverse_backend.repository.ReviewRepository;
import com.toyverse.toyverse_backend.service.implementation.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User user;
    private ReviewRequestDto reviewRequest;
    private Review review;
    private Review savedReview;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        reviewRequest = new ReviewRequestDto();
        reviewRequest.setRating(5);
        reviewRequest.setComment("Great toy!");

        review = new Review();
        review.setReviewId(1L);
        review.setRating(5);
        review.setComment("Great toy!");
        review.setUser(user);
        review.setToy(new Toy(1L));

        savedReview = new Review();
        savedReview.setReviewId(1L);
        savedReview.setRating(5);
        savedReview.setComment("Great toy!");
        savedReview.setUser(user);
        savedReview.setToy(new Toy(1L));
    }

    @Test
    void createReview_ShouldReturnReviewResponse_WhenUserHasPurchasedToy() {
        // Given
        Long toyId = 1L;
        when(orderItemRepository.existsByOrder_User_IdAndToy_ToyId(1L, toyId)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        // When
        ReviewResponseDto result = reviewService.createReview(user, toyId, reviewRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getReviewId());
        assertEquals(5, result.getRating());
        assertEquals("Great toy!", result.getComment());
        assertEquals("testuser", result.getUsername());

        verify(orderItemRepository).existsByOrder_User_IdAndToy_ToyId(1L, toyId);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_ShouldThrowNotPurchasedException_WhenUserHasNotPurchasedToy() {
        // Given
        Long toyId = 1L;
        when(orderItemRepository.existsByOrder_User_IdAndToy_ToyId(1L, toyId)).thenReturn(false);

        // When & Then
        NotPurchasedException exception = assertThrows(
                NotPurchasedException.class,
                () -> reviewService.createReview(user, toyId, reviewRequest)
        );

        assertEquals("You must purchase this toy before reviewing it", exception.getMessage());
        verify(orderItemRepository).existsByOrder_User_IdAndToy_ToyId(1L, toyId);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_ShouldSaveReviewWithCorrectData() {
        // Given
        Long toyId = 1L;
        when(orderItemRepository.existsByOrder_User_IdAndToy_ToyId(1L, toyId)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        // When
        reviewService.createReview(user, toyId, reviewRequest);

        // Then
        verify(reviewRepository).save(argThat(savedReview -> 
            savedReview.getRating() == 5 &&
            savedReview.getComment().equals("Great toy!") &&
            savedReview.getUser().equals(user) &&
            savedReview.getToy().getToyId().equals(toyId)
        ));
    }

    @Test
    void createReview_ShouldHandleDifferentRatings() {
        // Given
        Long toyId = 1L;
        ReviewRequestDto lowRatingRequest = new ReviewRequestDto();
        lowRatingRequest.setRating(1);
        lowRatingRequest.setComment("Not good");

        Review lowRatingReview = new Review();
        lowRatingReview.setReviewId(2L);
        lowRatingReview.setRating(1);
        lowRatingReview.setComment("Not good");
        lowRatingReview.setUser(user);
        lowRatingReview.setToy(new Toy(toyId));

        when(orderItemRepository.existsByOrder_User_IdAndToy_ToyId(1L, toyId)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(lowRatingReview);

        // When
        ReviewResponseDto result = reviewService.createReview(user, toyId, lowRatingRequest);

        // Then
        assertEquals(1, result.getRating());
        assertEquals("Not good", result.getComment());
    }

    @Test
    void getReviewsByToyId_ShouldReturnReviewsList_WhenReviewsExist() {
        // Given
        Long toyId = 1L;
        
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        Review review1 = new Review();
        review1.setReviewId(1L);
        review1.setRating(5);
        review1.setComment("Excellent!");
        review1.setUser(user1);

        Review review2 = new Review();
        review2.setReviewId(2L);
        review2.setRating(4);
        review2.setComment("Good quality");
        review2.setUser(user2);

        List<Review> reviews = Arrays.asList(review1, review2);
        when(reviewRepository.findByToyToyId(toyId)).thenReturn(reviews);

        // When
        List<ReviewResponseDto> result = reviewService.getReviewsByToyId(toyId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        ReviewResponseDto response1 = result.get(0);
        assertEquals(1L, response1.getReviewId());
        assertEquals(5, response1.getRating());
        assertEquals("Excellent!", response1.getComment());
        assertEquals("user1", response1.getUsername());

        ReviewResponseDto response2 = result.get(1);
        assertEquals(2L, response2.getReviewId());
        assertEquals(4, response2.getRating());
        assertEquals("Good quality", response2.getComment());
        assertEquals("user2", response2.getUsername());

        verify(reviewRepository).findByToyToyId(toyId);
    }

    @Test
    void getReviewsByToyId_ShouldReturnEmptyList_WhenNoReviewsExist() {
        // Given
        Long toyId = 1L;
        when(reviewRepository.findByToyToyId(toyId)).thenReturn(Arrays.asList());

        // When
        List<ReviewResponseDto> result = reviewService.getReviewsByToyId(toyId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reviewRepository).findByToyToyId(toyId);
    }

    @Test
    void createReview_ShouldHandleNullComment() {
        // Given
        Long toyId = 1L;
        ReviewRequestDto requestWithNullComment = new ReviewRequestDto();
        requestWithNullComment.setRating(3);
        requestWithNullComment.setComment(null);

        Review reviewWithNullComment = new Review();
        reviewWithNullComment.setReviewId(1L);
        reviewWithNullComment.setRating(3);
        reviewWithNullComment.setComment(null);
        reviewWithNullComment.setUser(user);
        reviewWithNullComment.setToy(new Toy(toyId));

        when(orderItemRepository.existsByOrder_User_IdAndToy_ToyId(1L, toyId)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(reviewWithNullComment);

        // When
        ReviewResponseDto result = reviewService.createReview(user, toyId, requestWithNullComment);

        // Then
        assertEquals(3, result.getRating());
        assertNull(result.getComment());
    }

    @Test
    void createReview_ShouldHandleEmptyComment() {
        // Given
        Long toyId = 1L;
        ReviewRequestDto requestWithEmptyComment = new ReviewRequestDto();
        requestWithEmptyComment.setRating(3);
        requestWithEmptyComment.setComment("");

        Review reviewWithEmptyComment = new Review();
        reviewWithEmptyComment.setReviewId(1L);
        reviewWithEmptyComment.setRating(3);
        reviewWithEmptyComment.setComment("");
        reviewWithEmptyComment.setUser(user);
        reviewWithEmptyComment.setToy(new Toy(toyId));

        when(orderItemRepository.existsByOrder_User_IdAndToy_ToyId(1L, toyId)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(reviewWithEmptyComment);

        // When
        ReviewResponseDto result = reviewService.createReview(user, toyId, requestWithEmptyComment);

        // Then
        assertEquals(3, result.getRating());
        assertEquals("", result.getComment());
    }

    @Test
    void createReview_ShouldVerifyPurchaseForCorrectUserAndToy() {
        // Given
        Long toyId = 5L;
        User differentUser = new User();
        differentUser.setId(99L);
        differentUser.setUsername("differentuser");

        when(orderItemRepository.existsByOrder_User_IdAndToy_ToyId(99L, toyId)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        // When
        reviewService.createReview(differentUser, toyId, reviewRequest);

        // Then
        verify(orderItemRepository).existsByOrder_User_IdAndToy_ToyId(99L, toyId);
        verify(orderItemRepository, never()).existsByOrder_User_IdAndToy_ToyId(1L, toyId);
    }
} 