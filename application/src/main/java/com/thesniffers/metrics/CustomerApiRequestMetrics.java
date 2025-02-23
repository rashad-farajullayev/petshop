package com.thesniffers.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerApiRequestMetrics {

    private final Counter customerApiCalls;

    @Autowired
    public CustomerApiRequestMetrics(MeterRegistry meterRegistry) {
        this.customerApiCalls = Counter.builder("customer.api.calls")
                .description("Number of times the customer API is called")
                .register(meterRegistry);
    }

    // Call this method inside the controller
    public void incrementApiCall() {
        customerApiCalls.increment();
    }
}
