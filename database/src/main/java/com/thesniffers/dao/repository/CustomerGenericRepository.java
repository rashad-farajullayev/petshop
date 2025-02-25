package com.thesniffers.dao.repository;

import com.thesniffers.dao.model.Customer;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerGenericRepository extends GenericRepository<Customer> {
}
