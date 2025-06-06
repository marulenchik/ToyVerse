package com.toyverse.toyverse_backend.dto;

import com.toyverse.toyverse_backend.entity.Review;

public class ReviewMapper {
    public static ReviewResponse toDto(Review review) {
        ReviewResponse dto = new ReviewResponse();
        dto.setReviewId(review.getReviewId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUsername(review.getUser().getUsername());
        return dto;
    }
}
