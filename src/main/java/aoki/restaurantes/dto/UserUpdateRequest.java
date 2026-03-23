package aoki.restaurantes.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record UserUpdateRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String login,
        @NotNull UUID userTypeId,
        @NotNull AddressDto address
) {}
