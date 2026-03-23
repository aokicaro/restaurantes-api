package aoki.restaurantes.service;

import aoki.restaurantes.domain.Address;
import aoki.restaurantes.domain.MenuItem;
import aoki.restaurantes.domain.Restaurant;
import aoki.restaurantes.domain.User;
import aoki.restaurantes.dto.MenuItemCreateRequest;
import aoki.restaurantes.dto.MenuItemUpdateRequest;
import aoki.restaurantes.exception.NotFoundException;
import aoki.restaurantes.repository.MenuItemRepository;
import aoki.restaurantes.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    private MenuItemService menuItemService;

    private UUID restaurantId;
    private UUID itemId;
    private Restaurant restaurant;
    private MenuItem menuItem;
    private MenuItemCreateRequest createRequest;
    private MenuItemUpdateRequest updateRequest;

    @BeforeEach
    void setup() {
        menuItemService = new MenuItemService(menuItemRepository, restaurantRepository);

        restaurantId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setName("João Dono");

        restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Restaurante Teste");
        restaurant.setAddress(new Address("Rua A", "10", "Rio", "20000-000", "Loja"));
        restaurant.setCuisineType("Brasileira");
        restaurant.setOpeningHour("08:00 às 22:00");
        restaurant.setOwner(owner);

        menuItem = new MenuItem();
        menuItem.setId(itemId);
        menuItem.setRestaurant(restaurant);
        menuItem.setName("Feijoada");
        menuItem.setDescription("Feijoada completa");
        menuItem.setPrice(new BigDecimal("49.90"));
        menuItem.setDineInOnly(false);
        menuItem.setPhotoPath("/images/feijoada.jpg");

        createRequest = new MenuItemCreateRequest(
                "Feijoada",
                "Feijoada completa",
                new BigDecimal("49.90"),
                false,
                "/images/feijoada.jpg"
        );

        updateRequest = new MenuItemUpdateRequest(
                "Feijoada Premium",
                "Feijoada premium com acompanhamentos",
                new BigDecimal("59.90"),
                true,
                "/images/feijoada-premium.jpg"
        );
    }

    @Test
    void shouldCreateMenuItemSuccessfully() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(invocation -> {
            MenuItem saved = invocation.getArgument(0);
            saved.setId(itemId);
            return saved;
        });

        MenuItem result = menuItemService.createMenuItem(restaurantId, createRequest);

        assertNotNull(result);
        assertEquals("Feijoada", result.getName());
        assertEquals(new BigDecimal("49.90"), result.getPrice());
        assertEquals(restaurantId, result.getRestaurant().getId());

        verify(restaurantRepository).findById(restaurantId);
        verify(menuItemRepository).save(any(MenuItem.class));
    }

    @Test
    void shouldThrowNotFoundWhenRestaurantDoesNotExistOnCreate() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> menuItemService.createMenuItem(restaurantId, createRequest));

        assertEquals("Restaurante não encontrado", ex.getMessage());

        verify(restaurantRepository).findById(restaurantId);
        verify(menuItemRepository, never()).save(any());
    }

    @Test
    void shouldReturnMenuItemByRestaurantAndId() {
        when(menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)).thenReturn(Optional.of(menuItem));

        MenuItem result = menuItemService.getMenuItem(itemId, restaurantId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals("Feijoada", result.getName());

        verify(menuItemRepository).findByIdAndRestaurantId(itemId, restaurantId);
    }

    @Test
    void shouldThrowNotFoundWhenMenuItemDoesNotExistInRestaurant() {
        when(menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> menuItemService.getMenuItem(itemId, restaurantId));

        assertEquals("Item do cardápio não encontrado.", ex.getMessage());

        verify(menuItemRepository).findByIdAndRestaurantId(itemId, restaurantId);
    }

    @Test
    void shouldListMenuItemsByRestaurant() {
        when(menuItemRepository.findByRestaurantId(restaurantId)).thenReturn(List.of(menuItem));

        List<MenuItem> result = menuItemService.listMenuitem(restaurantId);

        assertEquals(1, result.size());
        assertEquals("Feijoada", result.get(0).getName());

        verify(menuItemRepository).findByRestaurantId(restaurantId);
    }

    @Test
    void shouldUpdateMenuItemSuccessfully() {
        when(menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)).thenReturn(Optional.of(menuItem));
        when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MenuItem result = menuItemService.updateMenuItem(itemId, restaurantId, updateRequest);

        assertNotNull(result);
        assertEquals("Feijoada Premium", result.getName());
        assertEquals("Feijoada premium com acompanhamentos", result.getDescription());
        assertEquals(new BigDecimal("59.90"), result.getPrice());
        assertTrue(result.isDineInOnly());
        assertEquals("/images/feijoada-premium.jpg", result.getPhotoPath());

        verify(menuItemRepository).findByIdAndRestaurantId(itemId, restaurantId);
        verify(menuItemRepository).save(menuItem);
    }

    @Test
    void shouldDeleteMenuItemSuccessfully() {
        when(menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId)).thenReturn(Optional.of(menuItem));

        menuItemService.deleteMenuItem(itemId, restaurantId);

        verify(menuItemRepository).findByIdAndRestaurantId(itemId, restaurantId);
        verify(menuItemRepository).delete(menuItem);
    }
}