package aoki.restaurantes.dto;

import aoki.restaurantes.domain.Address;
import jakarta.validation.constraints.NotBlank;

public record AddressDto(
        @NotBlank String street,
        @NotBlank String number,
        @NotBlank String city,
        @NotBlank String zipcode,
        String complement
){
    public static AddressDto from(Address address) {
        if (address == null) return null;
        return new AddressDto(address.getStreet(), address.getNumber(), address.getCity(), address.getZipcode(),address.getComplement());
    }
    public Address toEmbeddable(){
        return new Address(street,number,city,zipcode,complement);
    }
}
