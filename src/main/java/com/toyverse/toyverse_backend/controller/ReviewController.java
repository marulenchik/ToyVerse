package com.toyverse.toyverse_backend.controller;

import com.toyverse.toyverse_backend.dto.ReviewMapper;
import com.toyverse.toyverse_backend.dto.ReviewRequest;
import com.toyverse.toyverse_backend.dto.ReviewResponse;
import com.toyverse.toyverse_backend.entity.Review;
import com.toyverse.toyverse_backend.entity.Toy;
import com.toyverse.toyverse_backend.entity.User;
import com.toyverse.toyverse_backend.exception.NotPurchasedException;
import com.toyverse.toyverse_backend.security.SecurityUtils;
import com.toyverse.toyverse_backend.service.ReviewService;
import com.toyverse.toyverse_backend.service.ToyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/toys/{toyId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ToyService toyService;
    private final SecurityUtils securityUtils;

    public ReviewController(ReviewService reviewService,
                            ToyService toyService,
                            SecurityUtils securityUtils) {
        this.reviewService = reviewService;
        this.toyService = toyService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<?> createReview(
            @PathVariable Long toyId,
            @Valid @RequestBody ReviewRequest request
    ) {
        if (toyService.getToyById(toyId) == null) {
            return ResponseEntity.notFound().build();
        }

        User currentUser = securityUtils.getCurrentUser();

        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setUser(currentUser);
        review.setToy(new Toy(toyId));

        try {
            Review saved = reviewService.saveReview(review);
            ReviewResponse dto = ReviewMapper.toDto(saved);
            return ResponseEntity.ok(dto);
        } catch (NotPurchasedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long toyId) {
        List<ReviewResponse> result = reviewService.getReviewsByToyId(toyId);
        return ResponseEntity.ok(result);
    }
}
