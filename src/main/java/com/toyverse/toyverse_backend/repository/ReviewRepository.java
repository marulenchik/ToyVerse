package com.toyverse.toyverse_backend.repository;

import com.toyverse.toyverse_backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByToyToyId(Long toyId);
}