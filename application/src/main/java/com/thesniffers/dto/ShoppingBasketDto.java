package com.thesniffers.dto;

import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.UUID;

public record ShoppingBasketDto(
        UUID id,

        ZonedDateTime created,

        @NotNull(message = "Status cannot be null")
        String status,

        ZonedDateTime statusDate,

        @NotNull(message = "Customer ID cannot be null")
        UUID customerId
) {}
