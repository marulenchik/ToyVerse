package com.toyverse.toyverse_backend.service;

import com.toyverse.toyverse_backend.dto.ReviewRequestDto;
import com.toyverse.toyverse_backend.dto.ReviewResponseDto;
import com.toyverse.toyverse_backend.entity.User;

import java.util.List;

public interface ReviewService {
    ReviewResponseDto createReview(User user, Long toyId, ReviewRequestDto request);
    List<ReviewResponseDto> getReviewsByToyId(Long toyId);
}
