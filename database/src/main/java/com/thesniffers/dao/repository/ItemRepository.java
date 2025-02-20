package com.thesniffers.dao.repository;

import com.thesniffers.dao.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    // Find all items belonging to a specific shopping basket
    List<Item> findByShoppingBasketId(UUID shoppingBasketId);

    @Modifying
    @Query("DELETE FROM Item i WHERE i.shoppingBasket.id = :shoppingBasketId")
    int deleteAllByShoppingBasketId(UUID shoppingBasketId);
}
