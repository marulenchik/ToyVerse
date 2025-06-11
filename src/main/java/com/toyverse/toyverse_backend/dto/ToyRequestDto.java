package com.toyverse.toyverse_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ToyRequestDto {
    @NotBlank
    private String name;

    @NotBlank
    private String category;

    @NotBlank
    private String ageGroup;

    @Positive
    private Double price;

    private String description;
    private String imageUrl;

    @Min(0)
    private Integer stockQuantity;

}
