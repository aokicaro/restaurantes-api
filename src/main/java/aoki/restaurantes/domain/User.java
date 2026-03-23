package aoki.restaurantes.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_users_email", columnNames = "email"))
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_users_user_type"))
    private UserType userType;

    @Embedded
    private Address address;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_modified", nullable = false)
    private Instant lastModified;

    @PrePersist
    void prePersist() {
        var now = Instant.now();
        createdAt = now;
        lastModified = now;
    }

    @PreUpdate
    void preUpdate() {
        lastModified = Instant.now();
    }

}
