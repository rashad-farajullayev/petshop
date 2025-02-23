package com.thesniffers.service;

import com.thesniffers.dao.model.Customer;
import com.thesniffers.dao.repository.CustomerRepository;
import com.thesniffers.dto.CustomerDto;
import com.thesniffers.exception.CustomerNotFoundException;
import com.thesniffers.mapper.CustomerMapper;
import com.thesniffers.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<CustomerDto> getAllCustomers() {
        log.info("Fetching all customers");
        var customers = customerRepository.getAllCustomers(SecurityUtils.getCurrentUserToken(), SecurityUtils.isAdmin())
                .stream()
                .map(customerMapper::toDto)
                .toList();
        log.info("Fetched customers count: {}", customers.size());
        return customers;
    }

    public CustomerDto getCustomerById(UUID id) {
        log.info("Fetching customer with ID: {}", id);

        return customerRepository.getCustomerById(id, SecurityUtils.getCurrentUserToken(), SecurityUtils.isAdmin())
                .map(customerMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Customer with ID {} not found", id);
                    return new CustomerNotFoundException("Customer not found with ID: " + id);
                });
    }

    public CustomerDto createCustomer(CustomerDto dto) {
        log.info("Creating new customer: {}", dto.name());
        Customer customer = customerMapper.toEntity(dto);
        customer.setOwner(SecurityUtils.getCurrentUserToken());
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created with ID: {}", savedCustomer.getId());
        return customerMapper.toDto(savedCustomer);
    }

    public CustomerDto updateCustomer(UUID id, CustomerDto dto) {
        log.info("Updating customer with ID: {}", id);

        // Retrieve the customer or throw an exception if not found
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Customer with ID {} not found", id);
                    return new CustomerNotFoundException("Customer not found with ID: " + id);
                });

        // Check if the current user is ADMIN or owns the customer
        if (SecurityUtils.isAdmin() || SecurityUtils.getCurrentUserToken().equals(existingCustomer.getOwner())) {
            existingCustomer.setName(dto.name());
            existingCustomer.setTimezone(dto.timezone());
            var updatedCustomer = customerRepository.save(existingCustomer);
            log.info("Successfully updated customer with ID: {}", id);
            return customerMapper.toDto(updatedCustomer);
        } else {
            log.warn("Unauthorized attempt to update customer with ID: {} by user {}", id, SecurityUtils.getCurrentUserToken());
            throw new AccessDeniedException("You do not have permission to modify this customer.");
        }
    }



    public void deleteCustomer(UUID id) {
        log.info("Deleting customer with ID: {}", id);

        // Retrieve the customer or throw an exception if not found
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Customer with ID {} does not exist", id);
                    return new CustomerNotFoundException("Customer not found with ID: " + id);
                });

        // Retrieve current user token and role
        String currentUserToken = SecurityUtils.getCurrentUserToken();

        // Authorization check: Allow only Admins or the Customer Owner
        if (!SecurityUtils.isAdmin() && !currentUserToken.equals(existingCustomer.getOwner())) {
            log.warn("Unauthorized attempt by {} to delete customer ID: {}", currentUserToken, id);
            throw new AccessDeniedException("You do not have permission to delete this customer.");
        }

        customerRepository.deleteById(id);
        log.info("Customer with ID {} deleted successfully", id);
    }

}
