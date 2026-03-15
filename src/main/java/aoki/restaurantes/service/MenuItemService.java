package aoki.restaurantes.service;

import aoki.restaurantes.domain.MenuItem;
import aoki.restaurantes.dto.MenuItemCreateRequest;
import aoki.restaurantes.dto.MenuItemUpdateRequest;
import aoki.restaurantes.exception.NotFoundException;
import aoki.restaurantes.repository.MenuItemRepository;
import aoki.restaurantes.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;


    public MenuItemService(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public MenuItem createMenuItem(UUID restaurantId, MenuItemCreateRequest createRequest) {
        var restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new NotFoundException("Restaurante não encontrado"));
        var menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant);
        menuItem.setName(createRequest.name());
        menuItem.setDescription(createRequest.description());
        menuItem.setDineInOnly(createRequest.dineInOnly());
        menuItem.setPhotoPath(createRequest.photoPath());
        return menuItemRepository.save(menuItem);
    }

    public MenuItem getMenuItem(UUID menuItemId, UUID restaurantId){
        return menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)
                .orElseThrow(() -> new NotFoundException("Item do cardápio não encontrado."));
    }

    public List<MenuItem> listMenuitem(UUID restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    @Transactional
    public MenuItem updateMenuItem(UUID menuItemId, UUID restaurantId, MenuItemUpdateRequest updateRequest){
        var menuItem = getMenuItem(menuItemId, restaurantId);
        menuItem.setName(updateRequest.name());
        menuItem.setDescription(updateRequest.description());
        menuItem.setPrice(updateRequest.price());
        menuItem.setDineInOnly(updateRequest.dineInOnly());
        menuItem.setPhotoPath(updateRequest.photoPath());
        return  menuItemRepository.save(menuItem);
    }

    @Transactional
    public void deleteMenuItem(UUID menuItemId, UUID restaurantId){
        menuItemRepository.delete(getMenuItem(menuItemId, restaurantId));
    }
}
