package com.thesniffers.service;

import com.thesniffers.dao.model.BasketStatus;
import com.thesniffers.dao.model.ShoppingBasket;
import com.thesniffers.dao.repository.ItemRepository;
import com.thesniffers.dao.repository.ShoppingBasketRepository;
import com.thesniffers.dto.ShoppingBasketDto;
import com.thesniffers.exception.ResourceNotFoundException;
import com.thesniffers.mapper.ShoppingBasketMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
public class ShoppingBasketService {

    private final ShoppingBasketRepository shoppingBasketRepository;
    private final ItemRepository itemRepository;
    private final ShoppingBasketMapper shoppingBasketMapper;
    private final BasketItemService basketItemService;

    public ShoppingBasketService(ShoppingBasketRepository shoppingBasketRepository,
                                 ItemRepository itemRepository,
                                 ShoppingBasketMapper shoppingBasketMapper, BasketItemService basketItemService) {
        this.shoppingBasketRepository = shoppingBasketRepository;
        this.itemRepository = itemRepository;
        this.shoppingBasketMapper = shoppingBasketMapper;
        this.basketItemService = basketItemService;
    }

    public List<ShoppingBasketDto> getAllBaskets() {
        log.info("Fetching all shopping baskets");
        return shoppingBasketRepository.findAll()
                .stream()
                .map(shoppingBasketMapper::toDto)
                .toList();
    }

    public Optional<ShoppingBasketDto> getBasketById(UUID id) {
        log.info("Fetching shopping basket by id: {}", id);
        return shoppingBasketRepository.findById(id).map(shoppingBasketMapper::toDto);
    }

    public ShoppingBasketDto createBasket(ShoppingBasketDto dto) {
        log.info("Creating shopping basket: {}", dto);
        ShoppingBasket basket = shoppingBasketMapper.toEntity(dto);
        var savedBasketDto = shoppingBasketMapper.toDto(shoppingBasketRepository.save(basket));
        log.info("Successfully created shopping basket: {}", savedBasketDto);
        return savedBasketDto;
    }

    public void deleteBasket(UUID id) {
        log.info("Deleting shopping basket: {}", id);
        if (!shoppingBasketRepository.existsById(id)) {
            log.warn("Shopping basket with id {} does not exist", id);
            throw new ResourceNotFoundException("Shopping basket with ID " + id + " not found.");
        }
        shoppingBasketRepository.deleteById(id);
    }

    // Update Status Separately
    public Optional<ShoppingBasketDto> updateStatus(UUID id, String status) {
        log.info("Updating shopping basket: {} into status: {}", id, status);
        return shoppingBasketRepository.findById(id)
                .map(existingBasket -> {
                    existingBasket.setStatus(BasketStatus.valueOf(status.toUpperCase()));
                    existingBasket.setStatusDate(ZonedDateTime.now());
                    var updatedBasket = shoppingBasketMapper.toDto(shoppingBasketRepository.save(existingBasket));
                    log.info("Successfully updated status of shopping basket: {} into status: {}", id, status);
                    return updatedBasket;
                });
    }

    // Checkout Basket (Mark as PAID)
    public Optional<ShoppingBasketDto> checkoutBasket(UUID id) {
        log.info("Checking out shopping basket: {}", id);
        return shoppingBasketRepository.findById(id)
                .map(existingBasket -> {
                    existingBasket.setStatus(BasketStatus.PAID);
                    existingBasket.setStatusDate(ZonedDateTime.now());
                    var savedBasketDto = shoppingBasketMapper.toDto(shoppingBasketRepository.save(existingBasket));
                    log.info("Successfully checked out shopping basket: {}", savedBasketDto);
                    return savedBasketDto;
                });
    }

    // Clear a Basket (Remove all items)
    public void clearBasket(UUID id) {
        log.info("Clearing shopping basket: {}", id);
        var count = itemRepository.deleteAllByShoppingBasketId(id);
        log.info("Successfully cleared shopping basket with ID: {}. Number of deleted items: {}", id, count);
    }

    // Get Total Items in a Basket
    public Integer getTotalItems(UUID id) {
        log.info("Getting shopping basket items: {}", id);
        return itemRepository.findByShoppingBasketId(id).size();
    }
}
