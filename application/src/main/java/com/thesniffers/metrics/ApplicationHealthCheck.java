package com.thesniffers.metrics;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ApplicationHealthCheck implements HealthIndicator {

    @Override
    public Health health() {
        if (isExternalServiceUp()) {
            return Health.up().withDetail("externalService", "Connected").build();
        } else {
            return Health.down().withDetail("externalService", "Cannot connect").build();
        }
    }

    private boolean isExternalServiceUp() {
        // Here I am mocking connection to external service is valid.
        // if our service has any other external services to connect to
        // be considered healthy and ready for start accepting connections
        return true;
    }
}
