package aoki.restaurantes.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RestaurantCreateRequest(
        @NotBlank String name,
        @Valid @NotNull AddressDto address,
        @NotBlank String cuisineType,
        @NotBlank String openingHours,
        @NotNull UUID ownerUserId
) {}
