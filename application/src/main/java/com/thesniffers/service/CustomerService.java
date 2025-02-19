package com.thesniffers.service;

import com.thesniffers.dao.model.Customer;
import com.thesniffers.dao.repository.CustomerRepository;
import com.thesniffers.dto.CustomerDto;
import com.thesniffers.exception.CustomerNotFoundException;
import com.thesniffers.mapper.CustomerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDto)
                .toList();
    }

    public Optional<CustomerDto> getCustomerById(UUID id) {
        return customerRepository.findById(id).map(customerMapper::toDto);
    }

    public CustomerDto createCustomer(CustomerDto dto) {
        Customer customer = customerMapper.toEntity(dto);
        customer.setOwner("rashad");
        return customerMapper.toDto(customerRepository.save(customer));
    }

    public CustomerDto updateCustomer(UUID id, CustomerDto dto) {
        return customerRepository.findById(id)
                .map(existingCustomer -> {
                    existingCustomer.setName(dto.name());
                    existingCustomer.setTimezone(dto.timezone());

                    return customerMapper.toDto(customerRepository.save(existingCustomer));
                })
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));
    }

    public void deleteCustomer(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
    }
}
