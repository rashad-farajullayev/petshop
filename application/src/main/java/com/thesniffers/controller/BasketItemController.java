package com.thesniffers.controller;

import com.thesniffers.dto.BasketItemDto;
import com.thesniffers.metrics.BasketItemApiRequestMetrics;
import com.thesniffers.service.BasketItemService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/items",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class BasketItemController {

    private final BasketItemService basketItemService;
    private final BasketItemApiRequestMetrics basketItemApiRequestMetrics;

    public BasketItemController(BasketItemService basketItemService,
                                BasketItemApiRequestMetrics basketItemApiRequestMetrics) {
        this.basketItemService = basketItemService;
        this.basketItemApiRequestMetrics = basketItemApiRequestMetrics;
    }

    @GetMapping("/basket/{basketId}")
    public ResponseEntity<List<BasketItemDto>> getItemsByBasket(@PathVariable UUID basketId) {
        basketItemApiRequestMetrics.incrementApiCall();
        return ResponseEntity.ok(basketItemService.getItemsByBasketId(basketId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BasketItemDto> getItemById(@PathVariable UUID id) {
        basketItemApiRequestMetrics.incrementApiCall();
        return basketItemService.getBasketItemById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BasketItemDto> createItem(@Valid @RequestBody BasketItemDto dto) {
        basketItemApiRequestMetrics.incrementApiCall();
        return ResponseEntity.ok(basketItemService.createBasketItem(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BasketItemDto> updateItem(@PathVariable UUID id, @Valid @RequestBody BasketItemDto dto) {
        basketItemApiRequestMetrics.incrementApiCall();
        return ResponseEntity.ok(basketItemService.updateBasketItem(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        basketItemApiRequestMetrics.incrementApiCall();
        basketItemService.deleteBasketItem(id);
        return ResponseEntity.noContent().build();
    }
}
