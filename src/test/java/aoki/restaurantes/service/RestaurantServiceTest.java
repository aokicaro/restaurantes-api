package aoki.restaurantes.service;

import aoki.restaurantes.domain.Address;
import aoki.restaurantes.domain.Restaurant;
import aoki.restaurantes.domain.User;
import aoki.restaurantes.domain.UserType;
import aoki.restaurantes.dto.AddressDto;
import aoki.restaurantes.dto.RestaurantCreateRequest;
import aoki.restaurantes.dto.RestaurantUpdateRequest;
import aoki.restaurantes.exception.BadRequestException;
import aoki.restaurantes.exception.NotFoundException;
import aoki.restaurantes.repository.RestaurantRepository;
import aoki.restaurantes.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserRepository userRepository;

    private RestaurantService restaurantService;

    private UUID restaurantId;
    private UUID ownerId;
    private User owner;
    private Restaurant restaurant;
    private RestaurantCreateRequest createRequest;
    private RestaurantUpdateRequest updateRequest;

    @BeforeEach
    void setup() {
        restaurantService = new RestaurantService(restaurantRepository, userRepository);

        restaurantId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        UserType ownerType = new UserType();
        ownerType.setId(UUID.randomUUID());
        ownerType.setName("RESTAURANT_OWNER");

        owner = new User();
        owner.setId(ownerId);
        owner.setName("João Dono");
        owner.setEmail("joao@example.com");
        owner.setLogin("joao");
        owner.setUserType(ownerType);

        restaurant = new Restaurant();
        restaurant.setId(restaurantId);
        restaurant.setName("Restaurante do João");
        restaurant.setAddress(new Address("Rua A", "10", "Rio", "20000-000", "Loja"));
        restaurant.setCuisineType("Brasileira");
        restaurant.setOpeningHour("08:00 às 22:00");
        restaurant.setOwner(owner);

        createRequest = new RestaurantCreateRequest(
                "Restaurante do João",
                new AddressDto("Rua A", "10", "Rio", "20000-000", "Loja"),
                "Brasileira",
                "08:00 às 22:00",
                ownerId
        );

        updateRequest = new RestaurantUpdateRequest(
                "Restaurante Atualizado",
                new AddressDto("Rua B", "20", "Niterói", "24000-000", "Sala 2"),
                "Italiana",
                "10:00 às 23:00",
                ownerId
        );
    }

    @Test
    void shouldCreateRestaurantSuccessfully() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> {
            Restaurant saved = invocation.getArgument(0);
            saved.setId(restaurantId);
            return saved;
        });

        Restaurant result = restaurantService.createRestaurant(createRequest);

        assertNotNull(result);
        assertEquals("Restaurante do João", result.getName());
        assertEquals("Brasileira", result.getCuisineType());
        assertEquals(ownerId, result.getOwner().getId());

        verify(userRepository).findById(ownerId);
        verify(restaurantRepository).save(any(Restaurant.class));
    }

    @Test
    void shouldThrowNotFoundWhenOwnerDoesNotExist() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> restaurantService.createRestaurant(createRequest));

        assertEquals("Usuário dono não encontrado", ex.getMessage());

        verify(userRepository).findById(ownerId);
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestWhenOwnerIsNotRestaurantOwner() {
        UserType clientType = new UserType();
        clientType.setId(UUID.randomUUID());
        clientType.setName("CLIENT");

        owner.setUserType(clientType);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> restaurantService.createRestaurant(createRequest));

        assertEquals("O usuário informado não é RESTAURANT_OWNER.", ex.getMessage());

        verify(userRepository).findById(ownerId);
        verify(restaurantRepository, never()).save(any());
    }

    @Test
    void shouldReturnRestaurantById() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        Restaurant result = restaurantService.getRestaurant(restaurantId);

        assertNotNull(result);
        assertEquals(restaurantId, result.getId());
        assertEquals("Restaurante do João", result.getName());

        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    void shouldThrowNotFoundWhenRestaurantDoesNotExist() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> restaurantService.getRestaurant(restaurantId));

        assertEquals("Restaurante não encontrado.", ex.getMessage());

        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    void shouldListAllRestaurants() {
        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));

        List<Restaurant> result = restaurantService.listRestaurant();

        assertEquals(1, result.size());
        assertEquals("Restaurante do João", result.get(0).getName());

        verify(restaurantRepository).findAll();
    }

    @Test
    void shouldUpdateRestaurantSuccessfully() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Restaurant result = restaurantService.updateRestaurant(restaurantId, updateRequest);

        assertNotNull(result);
        assertEquals("Restaurante Atualizado", result.getName());
        assertEquals("Italiana", result.getCuisineType());
        assertEquals("10:00 às 23:00", result.getOpeningHour());
        assertEquals("Rua B", result.getAddress().getStreet());

        verify(restaurantRepository).findById(restaurantId);
        verify(userRepository).findById(ownerId);
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void shouldDeleteRestaurantSuccessfully() {
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        restaurantService.deleteRestaurant(restaurantId);

        verify(restaurantRepository).findById(restaurantId);
        verify(restaurantRepository).delete(restaurant);
    }
}
