package aoki.restaurantes.integration;

import aoki.restaurantes.domain.Address;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

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

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setup() {
        menuItemRepository.deleteAll();
        restaurantRepository.deleteAll();
        userRepository.deleteAll();
        userTypeRepository.deleteAll();

        UserType clientType = new UserType();
        clientType.setName("CLIENT");
        clientType = userTypeRepository.save(clientType);

        User user = new User();
        user.setName("Fernanda");
        user.setEmail("fer@example.com");
        user.setLogin("fer");
        user.setPasswordHash(encoder.encode("123456"));
        user.setUserType(clientType);
        user.setAddress(new Address("Rua A", "10", "Rio", "20000-000", "Apto 101"));
        user.setCreatedAt(Instant.now());
        user.setLastModified(Instant.now());

        userRepository.save(user);
    }

    @Test
    void shouldValidateLoginSuccessfully() throws Exception {
        String body = """
                {
                  "login": "fer",
                  "password": "123456"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void shouldReturnFalseWhenPasswordIsInvalid() throws Exception {
        String body = """
                {
                  "login": "fer",
                  "password": "senhaErrada"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void shouldReturnFalseWhenLoginDoesNotExist() throws Exception {
        String body = """
                {
                  "login": "naoexiste",
                  "password": "123456"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }
}