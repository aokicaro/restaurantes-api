package aoki.restaurantes.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "user_types", uniqueConstraints = @UniqueConstraint(name = "uk_user_types_name", columnNames = "name"))
public class UserType {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;
}
