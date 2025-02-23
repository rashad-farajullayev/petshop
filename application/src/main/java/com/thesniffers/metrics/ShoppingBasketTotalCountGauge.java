package com.thesniffers.metrics;

import com.thesniffers.dao.repository.ShoppingBasketRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShoppingBasketTotalCountGauge {

    private final ShoppingBasketRepository shoppingBasketRepository;

    @Autowired
    public ShoppingBasketTotalCountGauge(MeterRegistry meterRegistry, ShoppingBasketRepository shoppingBasketRepository) {
        this.shoppingBasketRepository = shoppingBasketRepository;

        // Register a gauge that fetches real-time total baskets count from DB
        meterRegistry.gauge("basket.total.count", this, ShoppingBasketTotalCountGauge::getTotalBasketsCount);
    }

    // Fetch total baskets count dynamically from the database
    private double getTotalBasketsCount() {
        return shoppingBasketRepository.count();
    }
}