package aoki.restaurantes.service;

import aoki.restaurantes.domain.User;
import aoki.restaurantes.dto.ChangePasswordRequest;
import aoki.restaurantes.dto.LoginRequest;
import aoki.restaurantes.dto.UserCreateRequest;
import aoki.restaurantes.dto.UserUpdateRequest;
import aoki.restaurantes.exception.ConflictException;
import aoki.restaurantes.exception.NotFoundException;
import aoki.restaurantes.repository.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository repo) { this.repo = repo; }

    @Transactional
    public User create(UserCreateRequest req) {
        if (repo.existsByEmail(req.email())) throw new ConflictException("E-mail ja cadastrado.");

        User u = new User();
        u.setName(req.name());
        u.setEmail(req.email());
        u.setLogin(req.login());
        u.setRole(req.role());
        u.setAddress(req.address());
        u.setPasswordHash(encoder.encode(req.password()));
        return repo.save(u);
    }

    public User get(UUID id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Usuario nao encontrado."));
    }

    public List<User> searchByName(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public User updateProfile(UUID id, UserUpdateRequest req) {
        User u = get(id);

        // Se trocar email, checa unicidade
        if (!u.getEmail().equalsIgnoreCase(req.email()) && repo.existsByEmail(req.email())) {
            throw new ConflictException("E-mail ja cadastrado.");
        }

        u.setName(req.name());
        u.setEmail(req.email());
        u.setLogin(req.login());
        u.setRole(req.role());
        u.setAddress(req.address());
        return repo.save(u);
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest req) throws BadRequestException {
        User u = get(id);

        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            throw new BadRequestException("Senha atual invalida.");
        }
        u.setPasswordHash(encoder.encode(req.newPassword()));
        repo.save(u);
    }

    @Transactional
    public void delete(UUID id) {
        User u = get(id);
        repo.delete(u);
    }

    public boolean validateLogin(LoginRequest req) {
        return repo.findByLogin(req.login())
                .map(u -> encoder.matches(req.password(), u.getPasswordHash()))
                .orElse(false);
    }
}
