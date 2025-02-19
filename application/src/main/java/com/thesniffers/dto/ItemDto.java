package com.thesniffers.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ItemDto(
        UUID id,

        @NotBlank(message = "Description cannot be blank")
        String description,

        @NotNull(message = "Amount cannot be null")
        @Min(value = 1, message = "Amount must be at least 1")
        Integer amount,

        @NotNull(message = "Shopping basket ID cannot be null")
        UUID shoppingBasketId
) {}
