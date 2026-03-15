package aoki.restaurantes.api;

import aoki.restaurantes.dto.MenuItemCreateRequest;
import aoki.restaurantes.dto.MenuItemResponse;
import aoki.restaurantes.dto.MenuItemUpdateRequest;
import aoki.restaurantes.service.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/restaurants/{restaurantId}/menu-items")
public class MenuItemController {
    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse createMenuItem(@PathVariable UUID restaurantID,
                                           @Valid @RequestBody MenuItemCreateRequest createRequest) {
        return MenuItemResponse.from(menuItemService.createMenuItem(restaurantID, createRequest));
    }

    @GetMapping("/{id}")
    public MenuItemResponse getMenuItem(@PathVariable UUID id, @PathVariable UUID restaurantId) {
        return MenuItemResponse.from(menuItemService.getMenuItem(id,restaurantId));
    }

    @GetMapping
    public List<MenuItemResponse> listMenuItems(@PathVariable UUID restaurantId){
        return menuItemService.listMenuitem(restaurantId).stream().map(MenuItemResponse::from).toList();
    }

    @PutMapping("/{id}")
    public MenuItemResponse updateMenuItem(@PathVariable UUID id,
                                           @PathVariable UUID restaurantId,
                                           @Valid @RequestBody MenuItemUpdateRequest updateRequest) {
        return  MenuItemResponse.from(menuItemService.updateMenuItem(id,restaurantId,updateRequest));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id, @PathVariable UUID restaurantId){
        menuItemService.deleteMenuItem(id, restaurantId);
    }


}
