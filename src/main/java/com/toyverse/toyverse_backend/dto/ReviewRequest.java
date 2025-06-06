package com.toyverse.toyverse_backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {
    @Min(1) @Max(5)
    private Integer rating;

    @Size(max = 500)
    private String comment;

}
