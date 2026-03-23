package aoki.restaurantes.service;

import aoki.restaurantes.domain.UserType;
import aoki.restaurantes.exception.ConflictException;
import aoki.restaurantes.exception.NotFoundException;
import aoki.restaurantes.repository.UserTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserTypeServiceTest {

    @Mock
    private UserTypeRepository userTypeRepository;

    @InjectMocks
    private UserTypeService userTypeService;

    private UUID id;
    private UserType userType;

    @BeforeEach
    void setup(){
        userTypeService = new UserTypeService(userTypeRepository);

        id = UUID.randomUUID();
        userType = new UserType();
        userType.setId(id);
        userType.setName("CLIENT");
    }

    @Test
    void shouldCreateUserTypeSuccessfully() {
        when(userTypeRepository.existsByNameIgnoreCase("CLIENT")).thenReturn(false);
        when(userTypeRepository.save(any(UserType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserType result = userTypeService.create("CLIENT");
        assertNotNull(result);
        assertEquals("CLIENT", result.getName());
        verify(userTypeRepository).existsByNameIgnoreCase("CLIENT");
        verify(userTypeRepository).save(any(UserType.class));
    }

    @Test
    void shouldThrowConflictWhenCreatingDuplicatedUserType() {
        when(userTypeRepository.existsByNameIgnoreCase("CLIENT")).thenReturn(true);
        ConflictException conflictException = assertThrows(ConflictException.class, () -> userTypeService.create("CLIENT"));

        assertEquals("Tipo de usuário já existe.", conflictException.getMessage());

        verify(userTypeRepository).existsByNameIgnoreCase("CLIENT");
        verify(userTypeRepository, never()).save(any());
    }

    @Test
    void shouldReturnUserTypeById() {
        when(userTypeRepository.findById(id)).thenReturn(Optional.of(userType));
        UserType result = userTypeService.findById(id);

        assertNotNull(result);
        assertEquals(id,result.getId());
        assertEquals("CLIENT", result.getName());

        verify(userTypeRepository).findById(id);
    }

    @Test
    void shouldThrowNotFoundUserTypeDoesNotExist() {
        when(userTypeRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> userTypeService.findById(id));

        assertEquals("Tipo de usuário não encontrado", notFoundException.getMessage());

        verify(userTypeRepository).findById(id);
    }

    @Test
    void shouldListAllUserTypes() {
        UserType owner = new UserType();
        owner.setId(UUID.randomUUID());
        owner.setName("RESTAURANT_OWNER");

        when(userTypeRepository.findAll()).thenReturn(List.of(userType, owner));

        List<UserType> result = userTypeService.findAll();

        assertEquals(2, result.size());
        verify(userTypeRepository).findAll();
    }

    @Test
    void shouldUpdateUserTypeSuccessfully() {
        when(userTypeRepository.findById(id)).thenReturn(Optional.of(userType));
        when(userTypeRepository.existsByNameIgnoreCase("RESTAURANT_OWNER")).thenReturn(false);
        when(userTypeRepository.save(any(UserType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserType result = userTypeService.update(id, "RESTAURANT_OWNER");

        assertEquals("RESTAURANT_OWNER", result.getName());

        verify(userTypeRepository).findById(id);
        verify(userTypeRepository).existsByNameIgnoreCase("RESTAURANT_OWNER");
        verify(userTypeRepository).save(userType);
    }

    @Test
    void shouldNotCheckDuplicateWhenUpdatingWithSameName() {
        when(userTypeRepository.findById(id)).thenReturn(Optional.of(userType));
        when(userTypeRepository.save(any(UserType.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserType result = userTypeService.update(id, "CLIENT");

        assertEquals("CLIENT", result.getName());

        verify(userTypeRepository).findById(id);
        verify(userTypeRepository, never()).existsByNameIgnoreCase(anyString());
        verify(userTypeRepository).save(userType);
    }

    @Test
    void shouldThrowConflictWhenUpdatingToDuplicatedName() {
        when(userTypeRepository.findById(id)).thenReturn(Optional.of(userType));
        when(userTypeRepository.existsByNameIgnoreCase("RESTAURANT_OWNER")).thenReturn(true);

        ConflictException conflictException = assertThrows(ConflictException.class, () -> userTypeService.update(id, "RESTAURANT_OWNER"));

        assertEquals("Tipo de usuário já existe.", conflictException.getMessage());

        verify(userTypeRepository).findById(id);
        verify(userTypeRepository).existsByNameIgnoreCase("RESTAURANT_OWNER");
        verify(userTypeRepository, never()).save(any());
    }

    @Test
    void shouldDeleteUserTypeSuccessfully() {
        when(userTypeRepository.findById(id)).thenReturn(Optional.of(userType));

        userTypeService.delete(id);

        verify(userTypeRepository).findById(id);
        verify(userTypeRepository).delete(userType);
    }




}
