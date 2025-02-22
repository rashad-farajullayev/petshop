package com.thesniffers.controller;

import com.thesniffers.dto.ShoppingBasketDto;
import com.thesniffers.service.ShoppingBasketService;
import com.thesniffers.validation.ValidBasketStatus;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping(value = "/api/v1/shopping-baskets",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class ShoppingBasketController {

    private final ShoppingBasketService shoppingBasketService;

    public ShoppingBasketController(ShoppingBasketService shoppingBasketService) {
        this.shoppingBasketService = shoppingBasketService;
    }

    @GetMapping
    public ResponseEntity<List<ShoppingBasketDto>> getAllBaskets() {
        return ResponseEntity.ok(shoppingBasketService.getAllBaskets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShoppingBasketDto> getBasketById(@PathVariable UUID id) {
        return shoppingBasketService.getBasketById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ShoppingBasketDto> createBasket(@Valid @RequestBody ShoppingBasketDto dto) {
        return ResponseEntity.ok(shoppingBasketService.createBasket(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBasket(@PathVariable UUID id) {
        shoppingBasketService.deleteBasket(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/update-status")
    public ResponseEntity<ShoppingBasketDto> updateStatus(
            @PathVariable UUID id,
            @RequestParam @ValidBasketStatus String status) {
        return shoppingBasketService.updateStatus(id, status.toUpperCase())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<ShoppingBasketDto> checkoutBasket(@PathVariable UUID id) {
        return shoppingBasketService.checkoutBasket(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/clear")
    public ResponseEntity<Void> clearBasket(@PathVariable UUID id) {
        shoppingBasketService.clearBasket(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/total-items")
    public ResponseEntity<Integer> getTotalItems(@PathVariable UUID id) {
        return ResponseEntity.ok(shoppingBasketService.getTotalItems(id));
    }
}
