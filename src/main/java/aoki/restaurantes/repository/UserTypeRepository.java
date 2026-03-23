package aoki.restaurantes.repository;

import aoki.restaurantes.domain.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserTypeRepository extends JpaRepository<UserType, UUID> {
    boolean existsByNameIgnoreCase(String name);
    Optional<UserType> findByNameIgnoreCase(String name);
}
