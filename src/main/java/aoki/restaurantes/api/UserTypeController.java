package aoki.restaurantes.api;

import aoki.restaurantes.dto.UserTypeCreateRequest;
import aoki.restaurantes.dto.UserTypeResponse;
import aoki.restaurantes.service.UserTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/user-types")
public class UserTypeController {
    private final UserTypeService userTypeService;

    public UserTypeController(UserTypeService userTypeService){
        this.userTypeService = userTypeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserTypeResponse create(@Valid @RequestBody UserTypeCreateRequest createRequest){
        return UserTypeResponse.from(userTypeService.create(createRequest.name()));
    }

    @GetMapping("/{id}")
    public UserTypeResponse findById(@PathVariable UUID id) {
        return UserTypeResponse.from(userTypeService.findById(id));
    }

    @GetMapping
    public List<UserTypeResponse> findAll() {
        return userTypeService.findAll().stream().map(UserTypeResponse::from).toList();
    }

    @PutMapping("/{id}")
    public UserTypeResponse update(@PathVariable UUID id, @Valid @RequestBody UserTypeCreateRequest createRequest) {
        return UserTypeResponse.from(userTypeService.update(id, createRequest.name()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userTypeService.delete(id);
    }
}
