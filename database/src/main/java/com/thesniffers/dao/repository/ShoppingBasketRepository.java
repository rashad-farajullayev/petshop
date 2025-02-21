package com.thesniffers.dao.repository;

import com.thesniffers.dao.model.ShoppingBasket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingBasketRepository extends JpaRepository<ShoppingBasket, UUID> {

    // Find all baskets for a specific customer
    List<ShoppingBasket> findByCustomerId(UUID customerId);

    @Query("""
        SELECT b FROM ShoppingBasket b
        WHERE :isAdmin = true OR b.customer.owner = :ownerToken
    """)
    List<ShoppingBasket> findAllAccessibleBaskets(String ownerToken, boolean isAdmin);

    @Query("""
        SELECT b FROM ShoppingBasket b
        WHERE b.id = :id AND (:isAdmin = true OR b.customer.owner = :ownerToken)
    """)
    Optional<ShoppingBasket> findAccessibleBasketById(UUID id, String ownerToken, boolean isAdmin);

    @Query("""
        SELECT COUNT(b) > 0 FROM ShoppingBasket b
        WHERE b.id = :id AND (:isAdmin = true OR b.customer.owner = :ownerToken)
    """)
    boolean canDeleteBasket(UUID id, String ownerToken, boolean isAdmin);

    @Query("""
        SELECT COUNT(b) > 0 FROM ShoppingBasket b
        WHERE b.id = :id AND (:isAdmin = true OR b.customer.owner = :ownerToken)
    """)
    boolean canUpdateBasketStatus(UUID id, String ownerToken, boolean isAdmin);

    @Query("""
        SELECT COUNT(b) > 0 FROM ShoppingBasket b
        WHERE b.id = :id AND (:isAdmin = true OR b.customer.owner = :ownerToken)
    """)
    boolean canCheckoutBasket(UUID id, String ownerToken, boolean isAdmin);

    @Query("""
        SELECT COUNT(b) > 0 FROM ShoppingBasket b
        WHERE b.id = :id AND (:isAdmin = true OR b.customer.owner = :ownerToken)
    """)
    boolean canClearBasket(UUID id, String ownerToken, boolean isAdmin);

    @Query("""
        SELECT COUNT(b) > 0 FROM ShoppingBasket b
        WHERE b.id = :id AND (:isAdmin = true OR b.customer.owner = :ownerToken)
    """)
    boolean canViewBasketItems(UUID id, String ownerToken, boolean isAdmin);

}
