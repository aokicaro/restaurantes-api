package aoki.restaurantes.dto;

import aoki.restaurantes.domain.Address;
import aoki.restaurantes.domain.User;
import aoki.restaurantes.domain.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String login,
        UserRole role,
        AddressResponse addressResponse,
        Instant createdAt,
        Instant lastModified
) {

    public static UserResponse from(User user) {
        if (user == null) return null;

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getRole(),
                AddressResponse.from(user.getAddress()),
                user.getCreatedAt(),
                user.getLastModified()
        );
    }

    public record AddressResponse(
            String street,
            String number,
            String city,
            String zipCode,
            String complement
    ) {
        public static AddressResponse from(Address address) {
            if (address == null) return null;
            return new AddressResponse(
                    address.getStreet(),
                    address.getNumber(),
                    address.getCity(),
                    address.getZipcode(),
                    address.getComplement()
            );
        }
    }
}
