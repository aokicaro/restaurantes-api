package aoki.restaurantes.dto;

import aoki.restaurantes.domain.Address;
import aoki.restaurantes.domain.UserRole;
import jakarta.validation.constraints.*;

public record UserUpdateRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String login,
        @NotNull UserRole role,
        @NotNull Address address
) {}
