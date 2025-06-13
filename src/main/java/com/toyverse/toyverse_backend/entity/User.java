package com.toyverse.toyverse_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.*;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public User() {}
    public User(Long id) { this.id = id; }

}
