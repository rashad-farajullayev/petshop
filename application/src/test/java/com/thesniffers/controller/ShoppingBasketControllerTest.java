package com.thesniffers.controller;

import com.thesniffers.service.ShoppingBasketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ShoppingBasketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShoppingBasketService shoppingBasketService;

    private HttpHeaders tenant1Headers;
    private HttpHeaders tenant2Headers;
    private HttpHeaders adminHeaders;

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

        adminHeaders = new HttpHeaders();
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        adminHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        adminHeaders.set("Authorization", "Bearer admin-secret-token-123456");
    }

    @Test
    void testGetAllBaskets_AsAdmin_ShouldReturnAllBaskets() throws Exception {
        mockMvc.perform(get("/api/v1/shopping-baskets").headers(adminHeaders))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(8)); // 8 baskets in DB
    }

    @Test
    void testGetAllBaskets_AsTenant1_ShouldReturnOnlyTenant1Baskets() throws Exception {
        mockMvc.perform(get("/api/v1/shopping-baskets").headers(tenant1Headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(5)); // Tenant1 has 2 baskets
    }

    @Test
    void testGetBasketById_AsCorrectTenant_ShouldReturnBasket() throws Exception {
        mockMvc.perform(get("/api/v1/shopping-baskets/f7d4c6b9-dfe9-4c47-8e4d-d60cf9f6a6a1")
                        .headers(tenant1Headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetBasketById_AsWrongTenant_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/shopping-baskets/f7d4c6b9-dfe9-4c47-8e4d-d60cf9f6a6a1")
                        .headers(tenant2Headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateBasket_ShouldCreateBasket() throws Exception {
        String newBasketJson = """
                {
                    "status": "NEW",
                    "customerId": "52e88c2f-9c96-48e7-84dc-6c4c38dff25f"
                }
                """;

        mockMvc.perform(post("/api/v1/shopping-baskets")
                        .headers(tenant1Headers)
                        .content(newBasketJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void testUpdateBasketStatus_AsOwner_ShouldUpdateBasket() throws Exception {
        mockMvc.perform(patch("/api/v1/shopping-baskets/f7d4c6b9-dfe9-4c47-8e4d-d60cf9f6a6a1/update-status")
                        .headers(tenant1Headers)
                        .param("status", "PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void testUpdateBasketStatus_AsDifferentTenant_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/api/v1/shopping-baskets/f7d4c6b9-dfe9-4c47-8e4d-d60cf9f6a6a1/update-status")
                        .headers(tenant2Headers)
                        .param("status", "PAID"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCheckoutBasket_AsOwner_ShouldCheckoutSuccessfully() throws Exception {
        mockMvc.perform(post("/api/v1/shopping-baskets/f7d4c6b9-dfe9-4c47-8e4d-d60cf9f6a6a1/checkout")
                        .headers(tenant1Headers))
                .andExpect(status().isOk());
    }

    @Test
    void testCheckoutBasket_AsDifferentTenant_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/shopping-baskets/f7d4c6b9-dfe9-4c47-8e4d-d60cf9f6a6a1/checkout")
                        .headers(tenant2Headers))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteBasket_AsOwner_ShouldDeleteBasket() throws Exception {
        mockMvc.perform(delete("/api/v1/shopping-baskets/f7d4c6b9-dfe9-4c47-8e4d-d60cf9f6a6a1")
                        .headers(tenant1Headers))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteBasket_AsDifferentTenant_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/shopping-baskets/f7d4c6b9-dfe9-4c47-8e4d-d60cf9f6a6a1")
                        .headers(tenant2Headers))
                .andExpect(status().isForbidden());
    }

    @Test
    void testClearBasket_AsOwner_ShouldClearBasket() throws Exception {
        mockMvc.perform(delete("/api/v1/shopping-baskets/f7d4c6b9-dfe9-4c47-8e4d-d60cf9f6a6a1/clear")
                        .headers(tenant1Headers))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetTotalItems_AsOwner_ShouldReturnTotalItems() throws Exception {
        mockMvc.perform(get("/api/v1/shopping-baskets/f7d4c6b9-dfe9-4c47-8e4d-d60cf9f6a6a1/total-items")
                        .headers(tenant1Headers))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0));
    }

    @Test
    void testGetTotalItems_AsDifferentTenant_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/shopping-baskets/f7d4c6b9-dfe9-4c47-8e4d-d60cf9f6a6a1/total-items")
                        .headers(tenant2Headers))
                .andExpect(status().isForbidden());
    }
}
