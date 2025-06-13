package com.toyverse.toyverse_backend.repository;

import com.toyverse.toyverse_backend.entity.Toy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToyRepository extends JpaRepository<Toy, Long> {
    List<Toy> findByCategory(String category);
    List<Toy> findByAgeGroup(String ageGroup);
    List<Toy> findByCategoryAndAgeGroup(String category, String ageGroup);
}