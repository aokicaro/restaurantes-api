package aoki.restaurantes.dto;

import aoki.restaurantes.domain.Restaurant;

import java.util.UUID;

public record RestaurantResponse(
        UUID id,
        String name,
        AddressDto address,
        String cuisineType,
        String openingHours,
        UUID ownerUserId
) {
    public static RestaurantResponse from(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                AddressDto.from(restaurant.getAddress()),
                restaurant.getCuisineType(),
                restaurant.getOpeningHour(),
                restaurant.getOwner().getId()
        );
    }
}