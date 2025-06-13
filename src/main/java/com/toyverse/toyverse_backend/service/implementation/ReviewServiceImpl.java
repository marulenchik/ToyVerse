package com.toyverse.toyverse_backend.service.implementation;

import com.toyverse.toyverse_backend.dto.ReviewRequestDto;
import com.toyverse.toyverse_backend.dto.ReviewResponseDto;
import com.toyverse.toyverse_backend.entity.Review;
import com.toyverse.toyverse_backend.entity.Toy;
import com.toyverse.toyverse_backend.entity.User;
import com.toyverse.toyverse_backend.exception.NotPurchasedException;
import com.toyverse.toyverse_backend.repository.OrderItemRepository;
import com.toyverse.toyverse_backend.repository.ReviewRepository;
import com.toyverse.toyverse_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public ReviewResponseDto createReview(User user, Long toyId, ReviewRequestDto request) {
        if (!hasUserPurchasedToy(user.getId(), toyId)) {
            throw new NotPurchasedException("You must purchase this toy before reviewing it");
        }

        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setUser(user);
        review.setToy(new Toy(toyId));

        Review saved = reviewRepository.save(review);
        return mapToDto(saved);
    }

    @Override
    public List<ReviewResponseDto> getReviewsByToyId(Long toyId) {
        List<Review> reviews = reviewRepository.findByToyToyId(toyId);
        return reviews.stream().map(this::mapToDto).toList();
    }

    private boolean hasUserPurchasedToy(Long userId, Long toyId) {
        return orderItemRepository.existsByOrder_User_IdAndToy_ToyId(userId, toyId);
    }

    private ReviewResponseDto mapToDto(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setReviewId(review.getReviewId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUsername(review.getUser().getUsername());
        return dto;
    }
}
