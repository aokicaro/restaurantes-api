package aoki.restaurantes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MenuItemCreateRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @Positive BigDecimal price,
        boolean dineInOnly,
        @NotBlank String photoPath
) {}
