package aoki.restaurantes.dto;

import aoki.restaurantes.domain.MenuItem;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemResponse(
        UUID id,
        UUID restaurantId,
        String name,
        String description,
        BigDecimal price,
        boolean dineInOnly,
        String photoPath
) {
    public static MenuItemResponse from(MenuItem menuItem) {
        return new MenuItemResponse(
                menuItem.getId(),
                menuItem.getRestaurant().getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getPrice(),
                menuItem.isDineInOnly(),
                menuItem.getPhotoPath()
        );
    }
}