package com.thesniffers.metrics;

import com.thesniffers.dao.repository.CustomerRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerTotalCountGauge {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerTotalCountGauge(MeterRegistry meterRegistry, CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;

        // Register a gauge that fetches real-time total customer count from DB
        meterRegistry.gauge("customer.total.count", this, CustomerTotalCountGauge::getTotalCustomerCount);
    }

    private double getTotalCustomerCount() {
        return customerRepository.count();
    }
}