package com.thesniffers.dto;

import com.thesniffers.validation.ValidTimezone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.UUID;

public record CustomerDto(
        UUID id,

        @NotBlank(message = "Customer name cannot be empty")
        @Size(min = 3, max = 50, message = "Customer name must be between 3 and 50 characters")
        String name,

        @NotBlank(message = "Timezone cannot be empty")
        @ValidTimezone
        String timezone,

        ZonedDateTime created
) {}
