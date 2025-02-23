package com.thesniffers.controller;

import com.thesniffers.dto.CustomerDto;
import com.thesniffers.metrics.CustomerApiRequestMetrics;
import com.thesniffers.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/customers",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class    CustomerController {

    private final CustomerService customerService;
    private final CustomerApiRequestMetrics customerApiRequestMetrics;

    public CustomerController(CustomerService customerService,
                              CustomerApiRequestMetrics customerApiRequestMetrics) {
        this.customerService = customerService;
        this.customerApiRequestMetrics = customerApiRequestMetrics;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        customerApiRequestMetrics.incrementApiCall();
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable UUID id) {
        customerApiRequestMetrics.incrementApiCall();
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CustomerDto dto) {
        customerApiRequestMetrics.incrementApiCall();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createCustomer(dto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable UUID id, @Valid @RequestBody CustomerDto dto) {
        customerApiRequestMetrics.incrementApiCall();
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerApiRequestMetrics.incrementApiCall();
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}

