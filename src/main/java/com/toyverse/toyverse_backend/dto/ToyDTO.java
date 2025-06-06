package com.toyverse.toyverse_backend.dto;

import com.toyverse.toyverse_backend.entity.Toy;
import lombok.Data;

@Data
public class ToyDTO {
    private Long toyId;
    private String name;
    private String category;
    private String ageGroup;
    private Double price;
    private String description;
    private String imageUrl;
    private Integer stockQuantity;

    public ToyDTO(Toy toy) {
        this.toyId = toy.getToyId();
        this.name = toy.getName();
        this.category = toy.getCategory();
        this.ageGroup = toy.getAgeGroup();
        this.price = toy.getPrice();
        this.description = toy.getDescription();
        this.imageUrl = toy.getImageUrl();
        this.stockQuantity = toy.getStockQuantity();
    }
}
