package com.thesniffers.service;

import com.thesniffers.dao.model.Customer;
import com.thesniffers.dao.repository.CustomerRepository;
import com.thesniffers.dto.CustomerDto;
import com.thesniffers.exception.CustomerNotFoundException;
import com.thesniffers.mapper.CustomerMapper;
import com.thesniffers.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest extends ServiceTestBase{

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private UUID customerId;
    private Customer customer;
    private CustomerDto customerDto;
    private String tenantToken;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        tenantToken =TENANT_1_SECRET_TOKEN;

        customer = new Customer();
        customer.setId(customerId);
        customer.setName("John Doe");
        customer.setTimezone("UTC");
        customer.setOwner(tenantToken);

        customerDto = new CustomerDto(customerId, CUSTOMER_NAME, CUSTOMER_TIMEZONE, ZonedDateTime.now());

        customerRepository = Mockito.mock(CustomerRepository.class);
        customerMapper = Mockito.mock(CustomerMapper.class);
        customerService = new CustomerService(customerRepository, customerMapper);
    }

    @Test
    void getAllCustomers_shouldReturnCustomers() {
        mockCurrentUser();
        when(customerRepository.getAllCustomers(tenantToken, false)).thenReturn(List.of(customer));
        when(customerMapper.toDto(customer)).thenReturn(customerDto);

        List<CustomerDto> customers = customerService.getAllCustomers();

        assertEquals(1, customers.size());
        verify(customerRepository).getAllCustomers(tenantToken, false);
    }

    @Test
    void getCustomerById_shouldReturnCustomer() {
        mockCurrentUser();
        when(customerRepository.getCustomerById(customerId, tenantToken, false)).thenReturn(Optional.of(customer));
        when(customerMapper.toDto(customer)).thenReturn(customerDto);

        CustomerDto result = customerService.getCustomerById(customerId);

        assertEquals("John Doe", result.name());
    }

    @Test
    void getCustomerById_shouldThrowNotFoundException() {
        mockCurrentUser();
        when(customerRepository.getCustomerById(customerId, tenantToken, false)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(customerId));
    }

    @Test
    void createCustomer_shouldCreateCustomer() {
        mockCurrentUser();
        when(customerMapper.toEntity(customerDto)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toDto(customer)).thenReturn(customerDto);

        CustomerDto result = customerService.createCustomer(customerDto);

        assertEquals("John Doe", result.name());
        verify(customerRepository).save(customer);
    }

    @Test
    void updateCustomer_shouldUpdateCustomer() {
        // Use the existing mock setup
        mockCurrentUser();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // Capture the saved customer
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);

        when(customerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        when(customerMapper.toDto(any())).thenAnswer(invocation -> {
            Customer mappedCustomer = invocation.getArgument(0);
            return new CustomerDto(mappedCustomer.getId(), mappedCustomer.getName(), mappedCustomer.getTimezone(), ZonedDateTime.now());
        });

        CustomerDto updated = customerService.updateCustomer(customerId, new CustomerDto(customerId, "Updated Name", "UTC", ZonedDateTime.now()));

        verify(customerRepository).save(customerCaptor.capture());
        Customer savedCustomer = customerCaptor.getValue();

        // Assertions
        assertNotNull(savedCustomer, "Saved customer should not be null");
        assertEquals("Updated Name", savedCustomer.getName(), "Customer name should be updated");
        assertEquals("Updated Name", updated.name(), "DTO should reflect the updated name");
    }

    @Test
    void updateCustomer_shouldThrowAccessDenied_whenUnauthorizedUser() {
        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Simulate an unauthorized user (tenant2 trying to modify tenant1's customer)
            securityMock.when(SecurityUtils::getCurrentUserToken).thenReturn("tenant2-secret-token-ghijkl");
            securityMock.when(SecurityUtils::isAdmin).thenReturn(false);

            // Mock the repository to return an existing customer
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            // Assert that an AccessDeniedException is thrown
            assertThrows(AccessDeniedException.class, () -> customerService.updateCustomer(customerId, customerDto));
        }
    }


    @Test
    void deleteCustomer_shouldDeleteCustomer() {
        mockCurrentUser();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        customerService.deleteCustomer(customerId);

        verify(customerRepository).deleteById(customerId);
    }

    @Test
    void deleteCustomer_shouldThrowAccessDenied_whenUnauthorizedUser() {
        try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
            // Simulate an unauthorized user (tenant2 trying to delete tenant1's customer)
            securityMock.when(SecurityUtils::getCurrentUserToken).thenReturn("tenant2-secret-token-ghijkl");
            securityMock.when(SecurityUtils::isAdmin).thenReturn(false);

            // Mock the repository to return an existing customer
            when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            // Assert that an AccessDeniedException is thrown
            assertThrows(AccessDeniedException.class, () -> customerService.deleteCustomer(customerId));
        }
    }


    @Test
    void deleteCustomer_shouldThrowNotFoundException_whenCustomerDoesNotExist() {
        mockCurrentUser();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(customerId));
    }
}
