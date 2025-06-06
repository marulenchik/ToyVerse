package com.toyverse.toyverse_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Toy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long toyId;

    private String name;
    private String category;
    private String ageGroup;
    private Double price;
    private String description;
    private String imageUrl;
    private Integer stockQuantity;

    @OneToMany(mappedBy = "toy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public Toy(Long toyId) {
        this.toyId = toyId;
    }
}
