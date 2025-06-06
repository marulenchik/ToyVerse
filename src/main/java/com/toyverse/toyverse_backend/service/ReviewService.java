package com.toyverse.toyverse_backend.service;

import com.toyverse.toyverse_backend.dto.ReviewResponse;
import com.toyverse.toyverse_backend.entity.Review;

import java.util.List;

public interface ReviewService {
    Review saveReview(Review review);
    List<ReviewResponse> getReviewsByToyId(Long toyId);
}
