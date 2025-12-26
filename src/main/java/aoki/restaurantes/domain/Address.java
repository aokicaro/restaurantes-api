package aoki.restaurantes.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Embeddable
public class Address {
    private String street;
    private String number;
    private String city;
    private String zipcode;
    private String complement;
}
