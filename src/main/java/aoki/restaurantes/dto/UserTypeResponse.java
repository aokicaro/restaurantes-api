package aoki.restaurantes.dto;

import aoki.restaurantes.domain.UserType;
import java.util.UUID;

public record UserTypeResponse(
        UUID id,
        String name
) {
    public static UserTypeResponse from(UserType userType) {
        return new UserTypeResponse(userType.getId(), userType.getName());
    }
}