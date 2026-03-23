package aoki.restaurantes.integration;

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
class UserTypeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @BeforeEach
    void setup() {
        menuItemRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
        userTypeRepository.deleteAll();
    }

    @Test
    void shouldCreateUserTypeSuccessfully() throws Exception {
        String body = """
                {
                  "name": "CLIENT"
                }
                """;

        mockMvc.perform(post("/api/v1/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("CLIENT"));
    }

    @Test
    void shouldNotCreateDuplicatedUserType() throws Exception {
        UserType type = new UserType();
        type.setName("CLIENT");
        userTypeRepository.save(type);

        String body = """
                {
                  "name": "CLIENT"
                }
                """;

        mockMvc.perform(post("/api/v1/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail", containsString("Tipo de usuário já existe")));
    }

    @Test
    void shouldReturnUserTypeById() throws Exception {
        UserType type = new UserType();
        type.setName("RESTAURANT_OWNER");
        type = userTypeRepository.save(type);

        mockMvc.perform(get("/api/v1/user-types/{id}", type.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.name").value("RESTAURANT_OWNER"));
    }

    @Test
    void shouldReturnNotFoundWhenUserTypeDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/user-types/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"));
    }

    @Test
    void shouldListAllUserTypes() throws Exception {
        UserType a = new UserType();
        a.setName("CLIENT");

        UserType b = new UserType();
        b.setName("RESTAURANT_OWNER");

        userTypeRepository.save(a);
        userTypeRepository.save(b);

        mockMvc.perform(get("/api/v1/user-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldUpdateUserTypeSuccessfully() throws Exception {
        UserType type = new UserType();
        type.setName("CLIENT");
        type = userTypeRepository.save(type);

        String body = """
                {
                  "name": "CLIENT_VIP"
                }
                """;

        mockMvc.perform(put("/api/v1/user-types/{id}", type.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(type.getId().toString()))
                .andExpect(jsonPath("$.name").value("CLIENT_VIP"));
    }

    @Test
    void shouldDeleteUserTypeSuccessfully() throws Exception {
        UserType type = new UserType();
        type.setName("CLIENT");
        type = userTypeRepository.save(type);

        mockMvc.perform(delete("/api/v1/user-types/{id}", type.getId()))
                .andExpect(status().isNoContent());
    }
}