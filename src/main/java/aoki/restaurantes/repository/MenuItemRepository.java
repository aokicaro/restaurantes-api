package aoki.restaurantes.repository;

import aoki.restaurantes.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByRestaurantId(UUID restaurantId);
    Optional<MenuItem> findByIdAndRestaurantId(UUID id, UUID restaurantId);
}

