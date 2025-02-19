package com.thesniffers.service;

import com.thesniffers.dao.model.Item;
import com.thesniffers.dao.model.ShoppingBasket;
import com.thesniffers.dao.repository.ItemRepository;
import com.thesniffers.dao.repository.ShoppingBasketRepository;
import com.thesniffers.dto.BasketItemDto;
import com.thesniffers.exception.ResourceNotFoundException;
import com.thesniffers.mapper.BasketItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
public class BasketItemService {

    private final ItemRepository basketItemRepository;
    private final ShoppingBasketRepository shoppingBasketRepository;
    private final BasketItemMapper basketItemMapper;

    public BasketItemService(ItemRepository basketItemRepository, ShoppingBasketRepository shoppingBasketRepository, BasketItemMapper basketItemMapper) {
        this.basketItemRepository = basketItemRepository;
        this.shoppingBasketRepository = shoppingBasketRepository;
        this.basketItemMapper = basketItemMapper;
    }

    public List<BasketItemDto> getItemsByBasketId(UUID basketId) {
        return basketItemRepository.findByShoppingBasketId(basketId)
                .stream()
                .map(basketItemMapper::toDto)
                .toList();
    }

    public Optional<BasketItemDto> getItemById(UUID id) {
        return basketItemRepository.findById(id)
                .map(basketItemMapper::toDto);
    }

    public BasketItemDto createItem(BasketItemDto dto) {
        ShoppingBasket basket = shoppingBasketRepository.findById(dto.shoppingBasketId())
                .orElseThrow(() -> new ResourceNotFoundException("Shopping basket not found"));

        Item item = basketItemMapper.toEntity(dto);
        item.setShoppingBasket(basket);
        return basketItemMapper.toDto(basketItemRepository.save(item));
    }

    public BasketItemDto updateItem(UUID id, BasketItemDto dto) {
        return basketItemRepository.findById(id)
                .map(existingItem -> {
                    existingItem.setDescription(dto.description());
                    existingItem.setAmount(dto.amount());
                    return basketItemMapper.toDto(basketItemRepository.save(existingItem));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Basket item not found"));
    }

    public void deleteItem(UUID id) {
        if (!basketItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Basket item not found");
        }
        basketItemRepository.deleteById(id);
    }
}
