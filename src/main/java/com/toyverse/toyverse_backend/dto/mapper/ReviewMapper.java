package com.toyverse.toyverse_backend.dto.mapper;

import com.toyverse.toyverse_backend.dto.ReviewResponseDto;
import com.toyverse.toyverse_backend.entity.Review;

public class ReviewMapper {
    public static ReviewResponseDto toDto(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setReviewId(review.getReviewId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUsername(review.getUser().getUsername());
        return dto;
    }
}
