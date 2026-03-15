package aoki.restaurantes.api;

import aoki.restaurantes.dto.RestaurantCreateRequest;
import aoki.restaurantes.dto.RestaurantResponse;
import aoki.restaurantes.dto.RestaurantUpdateRequest;
import aoki.restaurantes.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse createRestaurant(@Valid @RequestBody RestaurantCreateRequest createRequest) {
        return RestaurantResponse.from(restaurantService.createRestaurant(
                createRequest
        ));
    }

    @GetMapping("/{id}")
    public RestaurantResponse getRestaurant(@PathVariable UUID id) {
        return RestaurantResponse.from(restaurantService.getRestaurant(id));
    }

    @GetMapping
    public List<RestaurantResponse> listRestaurants() {
        return restaurantService.listRestaurant().stream().map(RestaurantResponse::from).toList();
    }

    @PutMapping("/{id}")
    public RestaurantResponse updateRestaurant(@PathVariable UUID id, @Valid @RequestBody RestaurantUpdateRequest updateRequest){
        return RestaurantResponse.from(restaurantService.updateRestaurant(id, updateRequest));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRestaurant(@PathVariable UUID id){
        restaurantService.deleteRestaurant(id);
    }

}
