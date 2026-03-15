package aoki.restaurantes.service;

import aoki.restaurantes.domain.Restaurant;
import aoki.restaurantes.dto.RestaurantCreateRequest;
import aoki.restaurantes.dto.RestaurantUpdateRequest;
import aoki.restaurantes.exception.BadRequestException;
import aoki.restaurantes.exception.NotFoundException;
import aoki.restaurantes.repository.RestaurantRepository;
import aoki.restaurantes.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;


    public RestaurantService(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Restaurant createRestaurant(RestaurantCreateRequest createRequest) {
        var owner = userRepository.findById(createRequest.ownerUserId())
                .orElseThrow(() -> new NotFoundException("Usuário dono não encontrado"));

        //Owner needs to be RESTAURANT_OWNER
        if(owner.getUserType() == null || !"RESTAURANT_OWNER".equalsIgnoreCase(owner.getUserType().getName())){
            throw new BadRequestException("O usuário informado não é RESTAURANT_OWNER.");
        }
        var restaurant = new Restaurant();
        restaurant.setName(createRequest.name());
        restaurant.setAddress(createRequest.address().toEmbeddable());
        restaurant.setCuisineType(createRequest.cuisineType());
        restaurant.setOpeningHour(createRequest.openingHours());
        restaurant.setOwner(owner);
        return restaurantRepository.save(restaurant);
    }

    public Restaurant getRestaurant(UUID id) {
        return restaurantRepository.findById(id).orElseThrow(() -> new NotFoundException("Restaurante não encontrado."));
    }

    public List<Restaurant> listRestaurant() {
        return restaurantRepository.findAll();
    }

    @Transactional
    public Restaurant updateRestaurant(UUID id, RestaurantUpdateRequest updateRequest){
        var restaurant = getRestaurant(id);
        var owner = userRepository.findById(updateRequest.ownerUserId())
                .orElseThrow(() -> new NotFoundException("Usuério dono não encontrado"));

        if(owner.getUserType() == null || !"RESTAURANT_OWNER".equalsIgnoreCase(owner.getUserType().getName())){
            throw new BadRequestException("O usuário informado não é RESTAURANT_OWNER.");
        }

        restaurant.setName(updateRequest.name());
        restaurant.setAddress(updateRequest.address().toEmbeddable());
        restaurant.setCuisineType(updateRequest.cuisineType());
        restaurant.setOpeningHour(updateRequest.openingHours());
        restaurant.setOwner(owner);
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public void deleteRestaurant(UUID id){
        restaurantRepository.delete(getRestaurant(id));
    }


}
