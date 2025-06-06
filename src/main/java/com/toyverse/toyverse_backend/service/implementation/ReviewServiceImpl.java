package com.toyverse.toyverse_backend.service.implementation;

import com.toyverse.toyverse_backend.dto.ReviewResponse;
import com.toyverse.toyverse_backend.entity.Review;
import com.toyverse.toyverse_backend.exception.NotPurchasedException;
import com.toyverse.toyverse_backend.repository.OrderItemRepository;
import com.toyverse.toyverse_backend.repository.ReviewRepository;
import com.toyverse.toyverse_backend.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             OrderItemRepository orderItemRepository) {
        this.reviewRepository = reviewRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public Review saveReview(Review review) {
        Long userId = review.getUser().getId();
        Long toyId = review.getToy().getToyId();

        if (!hasUserPurchasedToy(userId, toyId)) {
            throw new NotPurchasedException("You must purchase this toy before reviewing it");
        }

        return reviewRepository.save(review);
    }

    private boolean hasUserPurchasedToy(Long userId, Long toyId) {
        return orderItemRepository.existsByOrder_User_IdAndToy_ToyId(userId, toyId);
    }

    @Override
    public List<ReviewResponse> getReviewsByToyId(Long toyId) {
        List<Review> reviews = reviewRepository.findByToyToyId(toyId);
        return reviews.stream().map(this::mapToDto).toList();
    }

    private ReviewResponse mapToDto(Review review) {
        ReviewResponse dto = new ReviewResponse();
        dto.setReviewId(review.getReviewId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUsername(review.getUser().getUsername());
        return dto;
    }
}
