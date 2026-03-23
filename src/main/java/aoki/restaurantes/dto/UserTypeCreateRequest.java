package aoki.restaurantes.dto;

import jakarta.validation.constraints.NotBlank;

public record UserTypeCreateRequest(
        @NotBlank String name
) {}
