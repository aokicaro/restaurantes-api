package aoki.restaurantes.service;

import aoki.restaurantes.domain.User;
import aoki.restaurantes.dto.ChangePasswordRequest;
import aoki.restaurantes.dto.LoginRequest;
import aoki.restaurantes.dto.UserCreateRequest;
import aoki.restaurantes.dto.UserUpdateRequest;
import aoki.restaurantes.exception.ConflictException;
import aoki.restaurantes.exception.NotFoundException;
import aoki.restaurantes.repository.UserRepository;

import aoki.restaurantes.exception.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import aoki.restaurantes.shared.Messages;

@Service
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository repository) { this.repository = repository; }

    @Transactional
    public User create(UserCreateRequest req) {

        if (repository.existsByEmail(req.email()))
            throw new ConflictException(Messages.User.EMAIL_ALREADY_EXISTS.getText());

        User u = new User();
        u.setName(req.name());
        u.setEmail(req.email());
        u.setLogin(req.login());
        u.setUserType(req.userType());
        u.setAddress(req.address());
        u.setPasswordHash(encoder.encode(req.password()));
        return repository.save(u);
    }

    public User findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(Messages.User.NOT_FOUND.getText()));
    }

    public List<User> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public User updateProfile(UUID id, UserUpdateRequest req) {
        User u = findById(id);

        // If change the email checks if it's unique
        if (!u.getEmail().equalsIgnoreCase(req.email()) && repository.existsByEmail(req.email())) {

            throw new ConflictException(Messages.User.EMAIL_ALREADY_EXISTS.getText());
        }

        u.setName(req.name());
        u.setEmail(req.email());
        u.setLogin(req.login());
        u.setUserType(req.userType());
        u.setAddress(req.address());
        return repository.save(u);
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest req) {
        User u = findById(id);

        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            throw new BadRequestException(Messages.User.INVALID_CURRENT_PASSWORD.getText());
        }
        u.setPasswordHash(encoder.encode(req.newPassword()));
        repository.save(u);
    }

    @Transactional
    public void delete(UUID id) {
        User u = findById(id);
        repository.delete(u);
    }

    public boolean validateLogin(LoginRequest req) {
        return repository.findByLogin(req.login())
                .map(u -> encoder.matches(req.password(), u.getPasswordHash()))
                .orElse(false);
    }
}
