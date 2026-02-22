package aoki.restaurantes.dto;

import aoki.restaurantes.dto.AddressResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RestaurantUpdateRequest(
        @NotBlank String name,
        @Valid @NotNull AddressResponse address,
        @NotBlank String cuisineType,
        @NotBlank String openingHours,
        @NotNull UUID ownerUserId
) {}
