package com.thesniffers.service;

import com.thesniffers.dao.model.Customer;
import com.thesniffers.dao.repository.CustomerRepository;
import com.thesniffers.dto.CustomerDto;
import com.thesniffers.exception.CustomerNotFoundException;
import com.thesniffers.mapper.CustomerMapper;
import lombok.extern.slf4j.Slf4j;
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
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDto)
                .toList();
    }

    public Optional<CustomerDto> getCustomerById(UUID id) {
        log.info("Fetching customer with ID: {}", id);
        return customerRepository.findById(id).map(customerMapper::toDto);
    }

    public CustomerDto createCustomer(CustomerDto dto) {
        log.info("Creating new customer: {}", dto.name());
        Customer customer = customerMapper.toEntity(dto);
        customer.setOwner("rashad");
        // TODO: add owner field here
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created with ID: {}", savedCustomer.getId());
        return customerMapper.toDto(savedCustomer);
    }

    public CustomerDto updateCustomer(UUID id, CustomerDto dto) {
        log.info("Updating customer with ID: {}", id);
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setName(dto.name());
                    existingCustomer.setTimezone(dto.timezone());
                    var updatedCustomer = customerRepository.save(existingCustomer);
                    log.info("Successfully updated customer with ID: {}", id);
                    return customerMapper.toDto(updatedCustomer);
                })
                .orElseThrow(() -> {
                    log.error("Customer with ID {} not found", id);
                    return new CustomerNotFoundException("Customer not found with ID: " + id);
                });
    }

    public void deleteCustomer(UUID id) {
        log.info("Deleting customer with ID: {}", id);
        if (!customerRepository.existsById(id)) {
            log.warn("Customer with ID {} does not exist", id);
            throw new CustomerNotFoundException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
        log.info("Customer with ID {} deleted successfully", id);
    }
}
