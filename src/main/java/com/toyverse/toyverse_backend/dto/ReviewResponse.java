package com.toyverse.toyverse_backend.dto;

import lombok.Data;

@Data
public class ReviewResponse {
    private Long reviewId;
    private Integer rating;
    private String comment;
    private String username;
}
