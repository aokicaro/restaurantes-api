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
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Address address;

    @Column(nullable = false)
    private String cuisineType;

    @Column(nullable = false)
    private String openingHour;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_restaurants_owner_user"))
    private User owner;
}
