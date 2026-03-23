package aoki.restaurantes.service;

import aoki.restaurantes.domain.Address;
import aoki.restaurantes.domain.User;
import aoki.restaurantes.domain.UserType;
import aoki.restaurantes.dto.AddressDto;
import aoki.restaurantes.dto.ChangePasswordRequest;
import aoki.restaurantes.dto.LoginRequest;
import aoki.restaurantes.dto.UserCreateRequest;
import aoki.restaurantes.dto.UserUpdateRequest;
import aoki.restaurantes.exception.BadRequestException;
import aoki.restaurantes.exception.ConflictException;
import aoki.restaurantes.exception.NotFoundException;
import aoki.restaurantes.repository.UserRepository;
import aoki.restaurantes.repository.UserTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserTypeRepository userTypeRepository;

    private UserService userService;

    private UUID userId;
    private UUID userTypeId;
    private User user;
    private UserType clientType;

    @BeforeEach
    void setup() {
        userService = new UserService(userRepository, userTypeRepository);

        userId = UUID.randomUUID();
        userTypeId = UUID.randomUUID();

        clientType = new UserType();
        clientType.setId(userTypeId);
        clientType.setName("CLIENTE");

        user = new User();
        user.setId(userId);
        user.setName("Fernanda");
        user.setEmail("fer@example.com");
        user.setLogin("fer");
        user.setPasswordHash(new BCryptPasswordEncoder().encode("123456"));
        user.setUserType(clientType);
        user.setAddress(new Address("Rua A", "10", "Rio", "20000-000", "Apto 101"));
    }

    @Test
    void shouldCreateUserSuccessfully() {
        UserCreateRequest request = new UserCreateRequest(
                "Fernanda",
                "fer@example.com",
                "fer",
                "123456",
                userTypeId,
                new AddressDto("Rua A", "10", "Rio", "20000-000", "Apto 101")
        );

        when(userRepository.existsByEmail("fer@example.com")).thenReturn(false);
        when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.of(clientType));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(userId);
            return saved;
        });

        User result = userService.create(request);

        assertNotNull(result);
        assertEquals("Fernanda", result.getName());
        assertEquals("fer@example.com", result.getEmail());
        assertEquals("CLIENTE", result.getUserType().getName());
        assertNotNull(result.getPasswordHash());

        verify(userRepository).existsByEmail("fer@example.com");
        verify(userTypeRepository).findById(userTypeId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowConflictWhenCreatingUserWithDuplicatedEmail() {
        UserCreateRequest request = new UserCreateRequest(
                "Fernanda",
                "fer@example.com",
                "fer",
                "123456",
                userTypeId,
                new AddressDto("Rua A", "10", "Rio", "20000-000", "Apto 101")
        );

        when(userRepository.existsByEmail("fer@example.com")).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> userService.create(request));

        assertEquals("E-mail ja cadastrado.", ex.getMessage());

        verify(userRepository).existsByEmail("fer@example.com");
        verify(userTypeRepository, never()).findById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReturnUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Fernanda", result.getName());

        verify(userRepository).findById(userId);
    }

    @Test
    void shouldThrowNotFoundWhenUserDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> userService.findById(userId));

        assertEquals("Usuario nao encontrado.", ex.getMessage());

        verify(userRepository).findById(userId);
    }

    @Test
    void shouldSearchUsersByName() {
        when(userRepository.findByNameContainingIgnoreCase("fer"))
                .thenReturn(List.of(user));

        List<User> result = userService.searchByName("fer");

        assertEquals(1, result.size());
        assertEquals("Fernanda", result.get(0).getName());

        verify(userRepository).findByNameContainingIgnoreCase("fer");
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        UserUpdateRequest request = new UserUpdateRequest(
                "Fernanda Atualizada",
                "fer@example.com",
                "fer",
                userTypeId,
                new AddressDto("Rua B", "20", "Niterói", "24000-000", "Apto 202")
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userTypeRepository.findById(userTypeId)).thenReturn(Optional.of(clientType));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateProfile(userId, request);

        assertNotNull(result);
        assertEquals("Fernanda Atualizada", result.getName());
        assertEquals("Rua B", result.getAddress().getStreet());

        verify(userRepository).findById(userId);
        verify(userTypeRepository).findById(userTypeId);
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowConflictWhenUpdatingToDuplicatedEmail() {
        UserUpdateRequest request = new UserUpdateRequest(
                "Fernanda Atualizada",
                "novoemail@example.com",
                "fer",
                userTypeId,
                new AddressDto("Rua B", "20", "Niterói", "24000-000", "Apto 202")
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("novoemail@example.com")).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> userService.updateProfile(userId, request));

        assertEquals("E-mail ja cadastrado.", ex.getMessage());

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("novoemail@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        ChangePasswordRequest request = new ChangePasswordRequest("123456", "654321");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.changePassword(userId, request);

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("654321", user.getPasswordHash()));
    }

    @Test
    void shouldThrowBadRequestWhenCurrentPasswordIsInvalid() {
        ChangePasswordRequest request = new ChangePasswordRequest("senhaErrada", "654321");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> userService.changePassword(userId, request));

        assertEquals("Senha atual invalida.", ex.getMessage());

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.delete(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    void shouldValidateLoginSuccessfully() {
        LoginRequest request = new LoginRequest("fer", "123456");

        when(userRepository.findByLogin("fer")).thenReturn(Optional.of(user));

        boolean result = userService.validateLogin(request);

        assertTrue(result);
        verify(userRepository).findByLogin("fer");
    }

    @Test
    void shouldReturnFalseWhenLoginIsInvalid() {
        LoginRequest request = new LoginRequest("fer", "senhaErrada");

        when(userRepository.findByLogin("fer")).thenReturn(Optional.of(user));

        boolean result = userService.validateLogin(request);

        assertFalse(result);
        verify(userRepository).findByLogin("fer");
    }

    @Test
    void shouldReturnFalseWhenUserLoginDoesNotExist() {
        LoginRequest request = new LoginRequest("naoexiste", "123456");

        when(userRepository.findByLogin("naoexiste")).thenReturn(Optional.empty());

        boolean result = userService.validateLogin(request);

        assertFalse(result);
        verify(userRepository).findByLogin("naoexiste");
    }
}