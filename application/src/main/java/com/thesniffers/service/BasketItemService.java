package com.thesniffers.service;

import com.thesniffers.dao.model.Item;
import com.thesniffers.dao.model.ShoppingBasket;
import com.thesniffers.dao.repository.BasketItemRepository;
import com.thesniffers.dao.repository.ShoppingBasketRepository;
import com.thesniffers.dto.BasketItemDto;
import com.thesniffers.exception.ResourceNotFoundException;
import com.thesniffers.mapper.BasketItemMapper;
import com.thesniffers.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
public class BasketItemService {

    private final BasketItemRepository basketItemRepository;
    private final ShoppingBasketRepository shoppingBasketRepository;
    private final BasketItemMapper basketItemMapper;

    public BasketItemService(BasketItemRepository basketItemRepository, ShoppingBasketRepository shoppingBasketRepository, BasketItemMapper basketItemMapper) {
        this.basketItemRepository = basketItemRepository;
        this.shoppingBasketRepository = shoppingBasketRepository;
        this.basketItemMapper = basketItemMapper;
    }

    public List<BasketItemDto> getItemsByBasketId(UUID basketId) {
        log.info("Fetching all basket items by basketId {}", basketId);

        // Get the current user's authentication details
        String currentUserToken = SecurityUtils.getCurrentUserToken();
        boolean isAdmin = SecurityUtils.isAdmin();

        // Check if the user has permission to view this basket's items
        if (!shoppingBasketRepository.canViewBasketItems(basketId, currentUserToken, isAdmin)) {
            log.warn("Unauthorized attempt by {} to view items in shopping basket: {}", currentUserToken, basketId);
            throw new AccessDeniedException("You do not have permission to view the items in this shopping basket.");
        }

        return basketItemRepository.findByShoppingBasketId(basketId)
                .stream()
                .map(basketItemMapper::toDto)
                .toList();
    }

    public Optional<BasketItemDto> getBasketItemById(UUID basketItemId) {
        log.info("Fetching basket item by id {}", basketItemId);

        // Fetch the item first
        Item existingItem = basketItemRepository.findById(basketItemId)
                .orElseThrow(() -> {
                    log.warn("BasketItem {} not found", basketItemId);
                    return new ResourceNotFoundException("Basket item not found");
                });

        // Get the current user's authentication details
        String currentUserToken = SecurityUtils.getCurrentUserToken();
        boolean isAdmin = SecurityUtils.isAdmin();

        // Check if the user has permission to view this item
        if (!isAdmin && !existingItem.getShoppingBasket().getCustomer().getOwner().equals(currentUserToken)) {
            log.warn("Unauthorized attempt by {} to view basket item: {}", currentUserToken, basketItemId);
            throw new AccessDeniedException("You do not have permission to view this basket item.");
        }

        return Optional.of(basketItemMapper.toDto(existingItem));
    }


    public BasketItemDto createBasketItem(BasketItemDto dto) {
        log.info("Creating item {}", dto);

        // Fetch the basket
        ShoppingBasket basket = shoppingBasketRepository.findById(dto.shoppingBasketId())
                .orElseThrow(() -> new ResourceNotFoundException("Shopping basket not found"));

        // Get the current user's authentication details
        String currentUserToken = SecurityUtils.getCurrentUserToken();
        boolean isAdmin = SecurityUtils.isAdmin();

        // Check if the user has permission to modify this basket
        if (!isAdmin && !basket.getCustomer().getOwner().equals(currentUserToken)) {
            log.warn("Unauthorized attempt by {} to add item to shopping basket: {}", currentUserToken, dto.shoppingBasketId());
            throw new AccessDeniedException("You do not have permission to add items to this shopping basket.");
        }

        // Convert DTO to Entity and save
        Item item = basketItemMapper.toEntity(dto);
        item.setShoppingBasket(basket);
        var savedItem = basketItemRepository.save(item);

        log.info("Saved item {}", savedItem);
        return basketItemMapper.toDto(savedItem);
    }

    public BasketItemDto updateBasketItem(UUID basketItemId, BasketItemDto dto) {
        log.info("Updating item {} into {}", basketItemId, dto);

        // Fetch the existing item
        Item existingItem = basketItemRepository.findById(basketItemId)
                .orElseThrow(() -> {
                    log.error("BasketItem {} not found", basketItemId);
                    return new ResourceNotFoundException("Basket item not found");
                });

        // Fetch the destination basket from DTO
        ShoppingBasket destinationBasket = shoppingBasketRepository.findById(dto.shoppingBasketId())
                .orElseThrow(() -> {
                    log.error("Destination shopping basket {} not found", dto.shoppingBasketId());
                    return new ResourceNotFoundException("Destination shopping basket not found");
                });

        // Get the current user's authentication details
        String currentUserToken = SecurityUtils.getCurrentUserToken();
        boolean isAdmin = SecurityUtils.isAdmin();

        if (!isAdmin) {
            String sourceOwner = existingItem.getShoppingBasket().getCustomer().getOwner();
            String destinationOwner = destinationBasket.getCustomer().getOwner();

            // Case 1: User is trying to modify another tenant's basket item
            if (!sourceOwner.equals(currentUserToken)) {
                log.warn("Unauthorized attempt by {} to update basket item {} owned by {}",
                        currentUserToken, basketItemId, sourceOwner);
                throw new AccessDeniedException("You do not have permission to update this basket item.");
            }

            // Case 2: User is assigning their item to another tenant’s basket
            if (!destinationOwner.equals(currentUserToken)) {
                log.warn("Unauthorized attempt by {} to move basket item {} to basket owned by {}",
                        currentUserToken, basketItemId, destinationOwner);
                throw new AccessDeniedException("You do not have permission to assign this basket item to another customer.");
            }
        }

        // Apply updates and save
        existingItem.setDescription(dto.description());
        existingItem.setAmount(dto.amount());
        existingItem.setShoppingBasket(destinationBasket); // Ensure item is assigned to the correct basket

        var savedItemDto = basketItemMapper.toDto(basketItemRepository.save(existingItem));

        log.info("Successfully updated basket item {} into {}", basketItemId, savedItemDto);
        return savedItemDto;
    }


    public void deleteBasketItem(UUID basketItemId) {
        log.info("Deleting BasketItem {}", basketItemId);

        // Fetch the existing item
        Item existingItem = basketItemRepository.findById(basketItemId)
                .orElseThrow(() -> {
                    log.warn("BasketItem {} not found", basketItemId);
                    return new ResourceNotFoundException("Basket item not found");
                });

        // Get the current user's authentication details
        String currentUserToken = SecurityUtils.getCurrentUserToken();
        boolean isAdmin = SecurityUtils.isAdmin();

        if (!isAdmin) {
            String ownerToken = existingItem.getShoppingBasket().getCustomer().getOwner();

            // Tenant tries to delete another tenant’s basket item
            if (!ownerToken.equals(currentUserToken)) {
                log.warn("Unauthorized attempt by {} to delete basket item {} owned by {}",
                        currentUserToken, basketItemId, ownerToken);
                throw new AccessDeniedException("You do not have permission to delete this basket item.");
            }
        }

        basketItemRepository.deleteById(basketItemId);
        log.info("Successfully deleted BasketItem {}", basketItemId);
    }


}
