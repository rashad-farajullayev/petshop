package com.thesniffers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesniffers.dto.BasketItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BasketItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private HttpHeaders tenant1Headers;
    private HttpHeaders tenant2Headers;

    private static final UUID TENANT1_BASKET_ID = UUID.fromString("f7d4c6b9-dfe9-4c47-8e4d-d60cf9f6a6a1");
    private static final UUID TENANT1_ITEM_ID = UUID.fromString("ee6a5f8d-b37f-4dda-a2dc-68c3b1f1e6fb");

    @BeforeEach
    void setUp() {
        tenant1Headers = new HttpHeaders();
        tenant1Headers.setContentType(MediaType.APPLICATION_JSON);
        tenant1Headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        tenant1Headers.set("Authorization", "Bearer tenant1-secret-token-abcdef");

        tenant2Headers = new HttpHeaders();
        tenant2Headers.setContentType(MediaType.APPLICATION_JSON);
        tenant2Headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        tenant2Headers.set("Authorization", "Bearer tenant2-secret-token-ghijkl");
    }

    @Test
    void testGetItemsByBasket_AsOwner_ShouldReturnItems() throws Exception {
        mockMvc.perform(get("/api/v1/items/basket/" + TENANT1_BASKET_ID)
                        .headers(tenant1Headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1)); // Tenant1's basket has 2 items
    }

    @Test
    void testGetItemsByBasket_AsDifferentTenant_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/items/basket/" + TENANT1_BASKET_ID)
                        .headers(tenant2Headers))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetItemById_AsDifferentTenant_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/items/" + TENANT1_ITEM_ID)
                        .headers(tenant2Headers))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateItem_AsOwner_ShouldCreateItem() throws Exception {
        BasketItemDto newItem = new BasketItemDto(null, "New Chew Toy", 2, TENANT1_BASKET_ID);
        String newItemJson = objectMapper.writeValueAsString(newItem);

        mockMvc.perform(post("/api/v1/items")
                        .headers(tenant1Headers)
                        .content(newItemJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("New Chew Toy"));
    }

    @Test
    void testCreateItem_AsDifferentTenant_ShouldReturnForbidden() throws Exception {
        BasketItemDto newItem = new BasketItemDto(null, "Unauthorized Item", 1, TENANT1_BASKET_ID);
        String newItemJson = objectMapper.writeValueAsString(newItem);

        mockMvc.perform(post("/api/v1/items")
                        .headers(tenant2Headers)
                        .content(newItemJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateItem_AsOwner_ShouldUpdateItem() throws Exception {
        BasketItemDto updatedItem = new BasketItemDto(TENANT1_ITEM_ID, "Updated Dog Food", 3, TENANT1_BASKET_ID);
        String updatedItemJson = objectMapper.writeValueAsString(updatedItem);

        mockMvc.perform(put("/api/v1/items/" + TENANT1_ITEM_ID)
                        .headers(tenant1Headers)
                        .content(updatedItemJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated Dog Food"))
                .andExpect(jsonPath("$.amount").value(3));
    }

    @Test
    void testUpdateItem_AsDifferentTenant_ShouldReturnNotFound() throws Exception {
        BasketItemDto updatedItem = new BasketItemDto(TENANT1_ITEM_ID, "Illegal Update", 3, TENANT1_BASKET_ID);
        String updatedItemJson = objectMapper.writeValueAsString(updatedItem);

        mockMvc.perform(put("/api/v1/items/" + TENANT1_ITEM_ID)
                        .headers(tenant2Headers)
                        .content(updatedItemJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteItem_AsOwner_ShouldDeleteItem() throws Exception {
        mockMvc.perform(delete("/api/v1/items/" + TENANT1_ITEM_ID)
                        .headers(tenant1Headers))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteItem_AsDifferentTenant_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/items/" + TENANT1_ITEM_ID)
                        .headers(tenant2Headers))
                .andExpect(status().isForbidden());
    }
}
