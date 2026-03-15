package aoki.restaurantes.dto;

import aoki.restaurantes.domain.Address;
import aoki.restaurantes.domain.UserType;
import jakarta.validation.constraints.*;

public record UserCreateRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String login,
        @NotBlank String password,
        @NotNull UserType userType,
        @NotNull Address address
) {}
