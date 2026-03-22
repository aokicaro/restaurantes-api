package aoki.restaurantes.integration;

import aoki.restaurantes.domain.Address;
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
class RestaurantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    private User ownerUser;
    private User clientUser;

    @BeforeEach
    void setup() {
        menuItemRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
        userTypeRepository.deleteAll();

        UserType ownerType = new UserType();
        ownerType.setName("RESTAURANT_OWNER");
        ownerType = userTypeRepository.save(ownerType);

        UserType clientType = new UserType();
        clientType.setName("CLIENT");
        clientType = userTypeRepository.save(clientType);

        ownerUser = new User();
        ownerUser.setName("João Dono");
        ownerUser.setEmail("joao.dono@example.com");
        ownerUser.setLogin("joaodono");
        ownerUser.setPasswordHash("hash");
        ownerUser.setUserType(ownerType);
        ownerUser.setAddress(new Address("Rua A", "10", "Rio", "20000-000", "Loja"));
        ownerUser.setCreatedAt(Instant.now());
        ownerUser.setLastModified(Instant.now());
        ownerUser = userRepository.save(ownerUser);

        clientUser = new User();
        clientUser.setName("Maria Cliente");
        clientUser.setEmail("maria.cliente@example.com");
        clientUser.setLogin("mariacliente");
        clientUser.setPasswordHash("hash");
        clientUser.setUserType(clientType);
        clientUser.setAddress(new Address("Rua B", "20", "Niterói", "24000-000", "Apto 2"));
        clientUser.setCreatedAt(Instant.now());
        clientUser.setLastModified(Instant.now());
        clientUser = userRepository.save(clientUser);
    }

    @Test
    void shouldCreateRestaurantSuccessfully() throws Exception {
        String body = """
                {
                  "name": "Restaurante do João",
                  "address": {
                    "street": "Rua A",
                    "number": "10",
                    "city": "Rio de Janeiro",
                    "zipcode": "20000-000",
                    "complement": "Loja"
                  },
                  "cuisineType": "Brasileira",
                  "openingHours": "08:00 às 22:00",
                  "ownerUserId": "%s"
                }
                """.formatted(ownerUser.getId());

        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Restaurante do João"))
                .andExpect(jsonPath("$.cuisineType").value("Brasileira"))
                .andExpect(jsonPath("$.ownerUserId").value(ownerUser.getId().toString()));
    }

    @Test
    void shouldReturnNotFoundWhenOwnerDoesNotExist() throws Exception {
        String body = """
                {
                  "name": "Restaurante X",
                  "address": {
                    "street": "Rua A",
                    "number": "10",
                    "city": "Rio de Janeiro",
                    "zipcode": "20000-000",
                    "complement": "Loja"
                  },
                  "cuisineType": "Brasileira",
                  "openingHours": "08:00 às 22:00",
                  "ownerUserId": "%s"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void shouldReturnBadRequestWhenOwnerIsNotRestaurantOwner() throws Exception {
        String body = """
                {
                  "name": "Restaurante X",
                  "address": {
                    "street": "Rua A",
                    "number": "10",
                    "city": "Rio de Janeiro",
                    "zipcode": "20000-000",
                    "complement": "Loja"
                  },
                  "cuisineType": "Brasileira",
                  "openingHours": "08:00 às 22:00",
                  "ownerUserId": "%s"
                }
                """.formatted(clientUser.getId());

        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad request"))
                .andExpect(jsonPath("$.detail", containsString("não é RESTAURANT_OWNER")));
    }

    @Test
    void shouldReturnRestaurantById() throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Restaurante Teste");
        restaurant.setAddress(new Address("Rua C", "30", "Rio", "21000-000", "Sala 1"));
        restaurant.setCuisineType("Italiana");
        restaurant.setOpeningHour("10:00 às 23:00");
        restaurant.setOwner(ownerUser);
        restaurant = restaurantRepository.save(restaurant);

        mockMvc.perform(get("/api/v1/restaurants/{id}", restaurant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurant.getId().toString()))
                .andExpect(jsonPath("$.name").value("Restaurante Teste"));
    }

    @Test
    void shouldListRestaurants() throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Restaurante Teste");
        restaurant.setAddress(new Address("Rua C", "30", "Rio", "21000-000", "Sala 1"));
        restaurant.setCuisineType("Italiana");
        restaurant.setOpeningHour("10:00 às 23:00");
        restaurant.setOwner(ownerUser);
        restaurantRepository.save(restaurant);

        mockMvc.perform(get("/api/v1/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldUpdateRestaurantSuccessfully() throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Restaurante Teste");
        restaurant.setAddress(new Address("Rua C", "30", "Rio", "21000-000", "Sala 1"));
        restaurant.setCuisineType("Italiana");
        restaurant.setOpeningHour("10:00 às 23:00");
        restaurant.setOwner(ownerUser);
        restaurant = restaurantRepository.save(restaurant);

        String body = """
                {
                  "name": "Restaurante Atualizado",
                  "address": {
                    "street": "Rua D",
                    "number": "40",
                    "city": "Niterói",
                    "zipcode": "24000-000",
                    "complement": "Loja 5"
                  },
                  "cuisineType": "Japonesa",
                  "openingHours": "11:00 às 23:30",
                  "ownerUserId": "%s"
                }
                """.formatted(ownerUser.getId());

        mockMvc.perform(put("/api/v1/restaurants/{id}", restaurant.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Restaurante Atualizado"))
                .andExpect(jsonPath("$.cuisineType").value("Japonesa"));
    }

    @Test
    void shouldDeleteRestaurantSuccessfully() throws Exception {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Restaurante Teste");
        restaurant.setAddress(new Address("Rua C", "30", "Rio", "21000-000", "Sala 1"));
        restaurant.setCuisineType("Italiana");
        restaurant.setOpeningHour("10:00 às 23:00");
        restaurant.setOwner(ownerUser);
        restaurant = restaurantRepository.save(restaurant);

        mockMvc.perform(delete("/api/v1/restaurants/{id}", restaurant.getId()))
                .andExpect(status().isNoContent());
    }
}