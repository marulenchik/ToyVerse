package com.toyverse.toyverse_backend.repository;

import com.toyverse.toyverse_backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByOrder_User_IdAndToy_ToyId(Long userId, Long toyId);
}
