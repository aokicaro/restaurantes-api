package aoki.restaurantes.service;

import aoki.restaurantes.dto.LoginRequest;
import aoki.restaurantes.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean validate(LoginRequest req) {
        return userRepository.findByLogin(req.login())
                .map(user -> encoder.matches(req.password(), user.getPasswordHash()))
                .orElse(false);
    }
}
