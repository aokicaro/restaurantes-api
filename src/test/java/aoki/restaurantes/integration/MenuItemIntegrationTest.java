package aoki.restaurantes.integration;

import aoki.restaurantes.domain.Address;
import aoki.restaurantes.domain.MenuItem;
import aoki.restaurantes.domain.Restaurant;
import aoki.restaurantes.domain.User;
import aoki.restaurantes.domain.UserType;
import aoki.restaurantes.repository.MenuItemRepository;
import aoki.restaurantes.repository.RestaurantRepository;
import aoki.restaurantes.repository.UserRepository;
import aoki.restaurantes.repository.UserTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MenuItemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    private Restaurant restaurant;

    @BeforeEach
    void setup() {
        menuItemRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
        userTypeRepository.deleteAll();

        UserType ownerType = new UserType();
        ownerType.setName("RESTAURANT_OWNER");
        ownerType = userTypeRepository.save(ownerType);

        User owner = new User();
        owner.setName("João Dono");
        owner.setEmail("joao.dono@example.com");
        owner.setLogin("joaodono");
        owner.setPasswordHash("hash");
        owner.setUserType(ownerType);
        owner.setAddress(new Address("Rua A", "10", "Rio", "20000-000", "Loja"));
        owner.setCreatedAt(Instant.now());
        owner.setLastModified(Instant.now());
        owner = userRepository.save(owner);

        restaurant = new Restaurant();
        restaurant.setName("Restaurante Teste");
        restaurant.setAddress(new Address("Rua B", "20", "Niterói", "24000-000", "Loja 2"));
        restaurant.setCuisineType("Brasileira");
        restaurant.setOpeningHour("08:00 às 22:00");
        restaurant.setOwner(owner);
        restaurant = restaurantRepository.save(restaurant);
    }

    @Test
    void shouldCreateMenuItemSuccessfully() throws Exception {
        String body = """
                {
                  "name": "Feijoada",
                  "description": "Feijoada completa",
                  "price": 49.90,
                  "dineInOnly": false,
                  "photoPath": "/images/feijoada.jpg"
                }
                """;

        mockMvc.perform(post("/api/v1/restaurants/{restaurantId}/menu-items", restaurant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.restaurantId").value(restaurant.getId().toString()))
                .andExpect(jsonPath("$.name").value("Feijoada"))
                .andExpect(jsonPath("$.price").value(49.90))
                .andExpect(jsonPath("$.dineInOnly").value(false));
    }

    @Test
    void shouldReturnNotFoundWhenRestaurantDoesNotExistOnCreate() throws Exception {
        String body = """
                {
                  "name": "Feijoada",
                  "description": "Feijoada completa",
                  "price": 49.90,
                  "dineInOnly": false,
                  "photoPath": "/images/feijoada.jpg"
                }
                """;

        mockMvc.perform(post("/api/v1/restaurants/{restaurantId}/menu-items", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void shouldReturnMenuItemByRestaurantAndId() throws Exception {
        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        item.setName("Feijoada");
        item.setDescription("Feijoada completa");
        item.setPrice(new BigDecimal("49.90"));
        item.setDineInOnly(false);
        item.setPhotoPath("/images/feijoada.jpg");
        item = menuItemRepository.save(item);

        mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/menu-items/{id}", restaurant.getId(), item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId().toString()))
                .andExpect(jsonPath("$.restaurantId").value(restaurant.getId().toString()))
                .andExpect(jsonPath("$.name").value("Feijoada"));
    }

    @Test
    void shouldReturnNotFoundWhenMenuItemDoesNotExistInRestaurant() throws Exception {
        mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/menu-items/{id}", restaurant.getId(), UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail", containsString("Item do cardápio não encontrado")));
    }

    @Test
    void shouldListMenuItemsByRestaurant() throws Exception {
        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        item.setName("Feijoada");
        item.setDescription("Feijoada completa");
        item.setPrice(new BigDecimal("49.90"));
        item.setDineInOnly(false);
        item.setPhotoPath("/images/feijoada.jpg");
        menuItemRepository.save(item);

        mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/menu-items", restaurant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldUpdateMenuItemSuccessfully() throws Exception {
        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        item.setName("Feijoada");
        item.setDescription("Feijoada completa");
        item.setPrice(new BigDecimal("49.90"));
        item.setDineInOnly(false);
        item.setPhotoPath("/images/feijoada.jpg");
        item = menuItemRepository.save(item);

        String body = """
                {
                  "name": "Feijoada Premium",
                  "description": "Feijoada premium com acompanhamentos",
                  "price": 59.90,
                  "dineInOnly": true,
                  "photoPath": "/images/feijoada-premium.jpg"
                }
                """;

        mockMvc.perform(put("/api/v1/restaurants/{restaurantId}/menu-items/{id}", restaurant.getId(), item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Feijoada Premium"))
                .andExpect(jsonPath("$.price").value(59.90))
                .andExpect(jsonPath("$.dineInOnly").value(true));
    }

    @Test
    void shouldDeleteMenuItemSuccessfully() throws Exception {
        MenuItem item = new MenuItem();
        item.setRestaurant(restaurant);
        item.setName("Feijoada");
        item.setDescription("Feijoada completa");
        item.setPrice(new BigDecimal("49.90"));
        item.setDineInOnly(false);
        item.setPhotoPath("/images/feijoada.jpg");
        item = menuItemRepository.save(item);

        mockMvc.perform(delete("/api/v1/restaurants/{restaurantId}/menu-items/{id}", restaurant.getId(), item.getId()))
                .andExpect(status().isNoContent());
    }
}