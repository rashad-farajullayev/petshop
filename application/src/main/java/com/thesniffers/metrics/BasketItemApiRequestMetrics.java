package com.thesniffers.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BasketItemApiRequestMetrics {

    private final Counter basketItemApiCalls;

    @Autowired
    public BasketItemApiRequestMetrics(MeterRegistry meterRegistry) {
        this.basketItemApiCalls = Counter.builder("item.api.calls")
                .description("Number of times the Basket Item API is called")
                .register(meterRegistry);
    }

    // Call this method inside the controller
    public void incrementApiCall() {
        basketItemApiCalls.increment();
    }
}