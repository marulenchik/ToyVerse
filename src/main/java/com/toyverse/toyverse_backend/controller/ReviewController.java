package com.toyverse.toyverse_backend.controller;

import com.toyverse.toyverse_backend.dto.ReviewRequestDto;
import com.toyverse.toyverse_backend.dto.ReviewResponseDto;
import com.toyverse.toyverse_backend.entity.User;
import com.toyverse.toyverse_backend.security.SecurityUtils;
import com.toyverse.toyverse_backend.service.ReviewService;
import com.toyverse.toyverse_backend.service.ToyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/toys/{toyId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ToyService toyService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
            @PathVariable Long toyId,
            @Valid @RequestBody ReviewRequestDto request
    ) {
        if (toyService.getToyById(toyId) == null) {
            return ResponseEntity.notFound().build();
        }

        User currentUser = securityUtils.getCurrentUser();

        ReviewResponseDto savedReview = reviewService.createReview(currentUser, toyId, request);
        return ResponseEntity.ok(savedReview);
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getReviews(@PathVariable Long toyId) {
        List<ReviewResponseDto> result = reviewService.getReviewsByToyId(toyId);
        return ResponseEntity.ok(result);
    }
}

