package aoki.restaurantes.repository;

import aoki.restaurantes.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    Optional<User> findByLogin(String login);
    List<User> findByNameContainingIgnoreCase(String name);
}
