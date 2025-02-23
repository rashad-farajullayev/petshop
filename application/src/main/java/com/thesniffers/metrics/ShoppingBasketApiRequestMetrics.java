package com.thesniffers.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShoppingBasketApiRequestMetrics {

    private final Counter shoppingBasketApiCalls;

    @Autowired
    public ShoppingBasketApiRequestMetrics(MeterRegistry meterRegistry) {
        this.shoppingBasketApiCalls = Counter.builder("basket.api.calls")
                .description("Number of times the Shopping Basket API is called")
                .register(meterRegistry);
    }

    // Call this method inside the controller
    public void incrementApiCall() {
        shoppingBasketApiCalls.increment();
    }
}