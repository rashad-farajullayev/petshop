package com.thesniffers.metrics;

import com.thesniffers.dao.repository.BasketItemRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BasketItemsTotalCountGauge {

    private final BasketItemRepository shoppingItemRepository;

    @Autowired
    public BasketItemsTotalCountGauge(MeterRegistry meterRegistry, BasketItemRepository shoppingItemRepository) {
        this.shoppingItemRepository = shoppingItemRepository;

        // Register a gauge that fetches real-time total shopping items count from DB
        meterRegistry.gauge("item.total.count", this, BasketItemsTotalCountGauge::getTotalBasketItemsCount);
    }

    private double getTotalBasketItemsCount() {
        return shoppingItemRepository.count();
    }
}