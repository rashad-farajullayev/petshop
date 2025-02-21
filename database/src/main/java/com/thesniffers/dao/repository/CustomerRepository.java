package com.thesniffers.dao.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.thesniffers.dao.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    public Customer findByName(String name);

    @Query("""
            SELECT i FROM Customer i
            WHERE :isAdmin = true OR i.owner = :ownerToken
    """)
    Collection<Customer> getAllCustomers(String ownerToken, boolean isAdmin);

    @Query("""
        SELECT i FROM Customer i
        WHERE i.id = :id
        AND (:isAdmin = true OR i.owner = :ownerToken)
    """)
    Optional<Customer> getCustomerById(UUID id, String ownerToken, boolean isAdmin);
}
