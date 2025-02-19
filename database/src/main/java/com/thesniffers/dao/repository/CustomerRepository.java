package com.thesniffers.dao.repository;

import com.thesniffers.dao.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, UUID> {
    public Customer findByName(String name);
}
