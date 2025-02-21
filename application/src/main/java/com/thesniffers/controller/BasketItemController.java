package com.thesniffers.controller;

import com.thesniffers.dto.BasketItemDto;
import com.thesniffers.service.BasketItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/items")
public class BasketItemController {

    private final BasketItemService basketItemService;

    public BasketItemController(BasketItemService basketItemService) {
        this.basketItemService = basketItemService;
    }

    @GetMapping("/basket/{basketId}")
    public ResponseEntity<List<BasketItemDto>> getItemsByBasket(@PathVariable UUID basketId) {
        return ResponseEntity.ok(basketItemService.getItemsByBasketId(basketId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BasketItemDto> getItemById(@PathVariable UUID id) {
        return basketItemService.getBasketItemById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BasketItemDto> createItem(@Valid @RequestBody BasketItemDto dto) {
        return ResponseEntity.ok(basketItemService.createBasketItem(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BasketItemDto> updateItem(@PathVariable UUID id, @Valid @RequestBody BasketItemDto dto) {
        return ResponseEntity.ok(basketItemService.updateBasketItem(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        basketItemService.deleteBasketItem(id);
        return ResponseEntity.noContent().build();
    }
}
