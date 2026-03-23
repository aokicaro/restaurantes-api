package aoki.restaurantes.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record UserCreateRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String login,
        @NotBlank String password,
        @NotNull UUID userTypeId,
        @NotNull AddressDto address
) {}
