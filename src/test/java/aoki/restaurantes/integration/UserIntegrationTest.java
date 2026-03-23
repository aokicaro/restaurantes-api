package aoki.restaurantes.integration;

import aoki.restaurantes.domain.*;
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

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserIntegrationTest {

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

    private UserType clientType;
    private UserType ownerType;

    @BeforeEach
    void setup() {
        menuItemRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
        userTypeRepository.deleteAll();

        clientType = new UserType();
        clientType.setName("CLIENT");
        clientType = userTypeRepository.save(clientType);

        ownerType = new UserType();
        ownerType.setName("RESTAURANT_OWNER");
        ownerType = userTypeRepository.save(ownerType);
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        String body = """
                {
                  "name": "Fernanda",
                  "email": "fer@example.com",
                  "login": "fer",
                  "password": "123456",
                  "userTypeId": "%s",
                  "address": {
                    "street": "Rua A",
                    "number": "10",
                    "city": "Rio de Janeiro",
                    "zipcode": "20000-000",
                    "complement": "Apto 101"
                  }
                }
                """.formatted(clientType.getId());

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Fernanda"))
                .andExpect(jsonPath("$.email").value("fer@example.com"));
    }

    @Test
    void shouldNotCreateUserWithDuplicatedEmail() throws Exception {
        User user = new User();
        user.setName("Fernanda");
        user.setEmail("fer@example.com");
        user.setLogin("fer");
        user.setPasswordHash("hash");
        user.setUserType(clientType);
        user.setAddress(new Address("Rua A", "10", "Rio", "20000-000", "Apto 101"));
        userRepository.save(user);

        String body = """
                {
                  "name": "Fernanda 2",
                  "email": "fer@example.com",
                  "login": "fer2",
                  "password": "123456",
                  "userTypeId": "%s",
                  "address": {
                    "street": "Rua B",
                    "number": "20",
                    "city": "Niterói",
                    "zipcode": "24000-000",
                    "complement": "Casa"
                  }
                }
                """.formatted(clientType.getId());

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail", containsString("E-mail ja cadastrado")));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        User user = new User();
        user.setName("Fernanda");
        user.setEmail("fer@example.com");
        user.setLogin("fer");
        user.setPasswordHash("hash");
        user.setUserType(clientType);
        user.setAddress(new Address("Rua A", "10", "Rio", "20000-000", "Apto 101"));
        user = userRepository.save(user);

        mockMvc.perform(get("/api/v1/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.name").value("Fernanda"))
                .andExpect(jsonPath("$.email").value("fer@example.com"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void shouldSearchUsersByName() throws Exception {
        User user = new User();
        user.setName("Fernanda");
        user.setEmail("fer@example.com");
        user.setLogin("fer");
        user.setPasswordHash("hash");
        user.setUserType(clientType);
        user.setAddress(new Address("Rua A", "10", "Rio", "20000-000", "Apto 101"));
        userRepository.save(user);

        mockMvc.perform(get("/api/v1/users").param("name", "fer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        User user = new User();
        user.setName("Fernanda");
        user.setEmail("fer@example.com");
        user.setLogin("fer");
        user.setPasswordHash("hash");
        user.setUserType(clientType);
        user.setAddress(new Address("Rua A", "10", "Rio", "20000-000", "Apto 101"));
        user = userRepository.save(user);

        String body = """
                {
                  "name": "Fernanda Atualizada",
                  "email": "fer@example.com",
                  "login": "fer",
                  "userTypeId": "%s",
                  "address": {
                    "street": "Rua B",
                    "number": "20",
                    "city": "Niterói",
                    "zipcode": "24000-000",
                    "complement": "Apto 202"
                  }
                }
                """.formatted(ownerType.getId());

        mockMvc.perform(put("/api/v1/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fernanda Atualizada"));
    }

    @Test
    void shouldChangePasswordSuccessfully() throws Exception {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        User user = new User();
        user.setName("Fernanda");
        user.setEmail("fer@example.com");
        user.setLogin("fer");
        user.setPasswordHash(encoder.encode("123456"));
        user.setUserType(clientType);
        user.setAddress(new Address("Rua A", "10", "Rio", "20000-000", "Apto 101"));
        user = userRepository.save(user);

        String body = """
                {
                  "password": "123456",
                  "newPassword": "654321"
                }
                """;

        mockMvc.perform(patch("/api/v1/users/{id}/password", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnBadRequestWhenCurrentPasswordIsInvalid() throws Exception {
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        User user = new User();
        user.setName("Fernanda");
        user.setEmail("fer@example.com");
        user.setLogin("fer");
        user.setPasswordHash(encoder.encode("123456"));
        user.setUserType(clientType);
        user.setAddress(new Address("Rua A", "10", "Rio", "20000-000", "Apto 101"));
        user = userRepository.save(user);

        String body = """
                {
                  "password": "senhaErrada",
                  "newPassword": "654321"
                }
                """;

        mockMvc.perform(patch("/api/v1/users/{id}/password", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad request"));
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        User user = new User();
        user.setName("Fernanda");
        user.setEmail("fer@example.com");
        user.setLogin("fer");
        user.setPasswordHash("hash");
        user.setUserType(clientType);
        user.setAddress(new Address("Rua A", "10", "Rio", "20000-000", "Apto 101"));
        user = userRepository.save(user);

        mockMvc.perform(delete("/api/v1/users/{id}", user.getId()))
                .andExpect(status().isNoContent());
    }
}