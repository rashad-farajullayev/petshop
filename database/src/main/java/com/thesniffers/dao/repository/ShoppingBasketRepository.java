package com.thesniffers.dao.repository;

import com.thesniffers.dao.model.ShoppingBasket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShoppingBasketRepository extends JpaRepository<ShoppingBasket, UUID> {

    // Find all baskets for a specific customer
    List<ShoppingBasket> findByCustomerId(UUID customerId);
}
