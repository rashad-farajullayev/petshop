package com.thesniffers.controller;

import com.thesniffers.service.CustomerService;
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
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerService customerService;

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
    void testGetAllCustomers_AsAdmin_ShouldReturnAllCustomers() throws Exception {
        mockMvc.perform(get("/api/v1/customers").headers(adminHeaders))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(7)); // 6 customers in DB
    }

    @Test
    void testGetAllCustomers_AsTenant1_ShouldReturnOnlyTenant1Customers() throws Exception {
        mockMvc.perform(get("/api/v1/customers").headers(tenant1Headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2)); // Tenant1 has 2 customers
    }

    @Test
    void testGetCustomerById_AsCorrectTenant_ShouldReturnCustomer() throws Exception {
        mockMvc.perform(get("/api/v1/customers/91ab4d34-ef20-46b2-b9a8-0cf5fef9f83a")
                        .headers(tenant1Headers))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Customer"));
    }

    @Test
    void testGetCustomerById_AsWrongTenant_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/customers/91ab4d34-ef20-46b2-b9a8-0cf5fef9f83a")
                        .headers(tenant2Headers))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateCustomer_ShouldCreateCustomer() throws Exception {
        String newCustomerJson = """
                {
                    "name": "New Customer",
                    "timezone": "UTC"
                }
                """;

        mockMvc.perform(post("/api/v1/customers")
                        .headers(adminHeaders)
                        .content(newCustomerJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("New Customer"));
    }

    @Test
    void testUpdateCustomer_AsOwner_ShouldUpdateCustomer() throws Exception {
        String updatedCustomerJson = """
                {
                    "name": "Updated Customer",
                    "timezone": "America/Los_Angeles"
                }
                """;

        mockMvc.perform(put("/api/v1/customers/91ab4d34-ef20-46b2-b9a8-0cf5fef9f83a")
                        .headers(tenant1Headers)
                        .content(updatedCustomerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Customer"))
                .andExpect(jsonPath("$.timezone").value("America/Los_Angeles"));
    }

    @Test
    void testUpdateCustomer_AsDifferentTenant_ShouldReturnForbidden() throws Exception {
        String updatedCustomerJson = """
                {
                    "name": "Invalid Update",
                    "timezone": "Europe/London"
                }
                """;

        mockMvc.perform(put("/api/v1/customers/91ab4d34-ef20-46b2-b9a8-0cf5fef9f83a")
                        .headers(tenant2Headers)
                        .content(updatedCustomerJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteCustomer_AsOwner_ShouldDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/91ab4d34-ef20-46b2-b9a8-0cf5fef9f83a")
                        .headers(tenant1Headers))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCustomer_AsDifferentTenant_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/91ab4d34-ef20-46b2-b9a8-0cf5fef9f83a")
                        .headers(tenant2Headers))
                .andExpect(status().isForbidden());
    }
}
