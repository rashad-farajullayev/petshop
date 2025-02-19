package com.thesniffers.service;

import com.thesniffers.dao.model.BasketStatus;
import com.thesniffers.dao.model.ShoppingBasket;
import com.thesniffers.dao.repository.ItemRepository;
import com.thesniffers.dao.repository.ShoppingBasketRepository;
import com.thesniffers.dto.ShoppingBasketDto;
import com.thesniffers.exception.ResourceNotFoundException;
import com.thesniffers.mapper.ShoppingBasketMapper;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShoppingBasketService {

    private final ShoppingBasketRepository shoppingBasketRepository;
    private final ItemRepository itemRepository;
    private final ShoppingBasketMapper shoppingBasketMapper;

    public ShoppingBasketService(ShoppingBasketRepository shoppingBasketRepository,
                                 ItemRepository itemRepository,
                                 ShoppingBasketMapper shoppingBasketMapper) {
        this.shoppingBasketRepository = shoppingBasketRepository;
        this.itemRepository = itemRepository;
        this.shoppingBasketMapper = shoppingBasketMapper;
    }

    public List<ShoppingBasketDto> getAllBaskets() {
        return shoppingBasketRepository.findAll()
                .stream()
                .map(shoppingBasketMapper::toDto)
                .toList();
    }

    public Optional<ShoppingBasketDto> getBasketById(UUID id) {
        return shoppingBasketRepository.findById(id).map(shoppingBasketMapper::toDto);
    }

    public ShoppingBasketDto createBasket(ShoppingBasketDto dto) {
        ShoppingBasket basket = shoppingBasketMapper.toEntity(dto);
        return shoppingBasketMapper.toDto(shoppingBasketRepository.save(basket));
    }

    public void deleteBasket(UUID id) {
        if (!shoppingBasketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Shopping basket with ID " + id + " not found.");
        }
        shoppingBasketRepository.deleteById(id);
    }

    // Update Status Separately
    public Optional<ShoppingBasketDto> updateStatus(UUID id, String status) {
        return shoppingBasketRepository.findById(id)
                .map(existingBasket -> {
                    existingBasket.setStatus(BasketStatus.valueOf(status.toUpperCase()));
                    existingBasket.setStatusDate(ZonedDateTime.now());
                    return shoppingBasketMapper.toDto(shoppingBasketRepository.save(existingBasket));
                });
    }

    // Checkout Basket (Mark as PAID)
    public Optional<ShoppingBasketDto> checkoutBasket(UUID id) {
        return shoppingBasketRepository.findById(id)
                .map(existingBasket -> {
                    existingBasket.setStatus(BasketStatus.PAID);
                    existingBasket.setStatusDate(ZonedDateTime.now());
                    return shoppingBasketMapper.toDto(shoppingBasketRepository.save(existingBasket));
                });
    }

    // Clear a Basket (Remove all items)
    public void clearBasket(UUID id) {
        shoppingBasketRepository.findById(id).ifPresent(basket -> {
            itemRepository.deleteAll(basket.getItems());
        });
    }

    // Get Total Items in a Basket
    public Integer getTotalItems(UUID id) {
        return itemRepository.findByShoppingBasketId(id).size();
    }
}
