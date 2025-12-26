package aoki.restaurantes.api;

import aoki.restaurantes.dto.ChangePasswordRequest;
import aoki.restaurantes.dto.UserCreateRequest;
import aoki.restaurantes.dto.UserResponse;
import aoki.restaurantes.dto.UserUpdateRequest;
import aoki.restaurantes.service.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserCreateRequest req) {
        return UserResponse.from(service.create(req));
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable UUID id) {
        return UserResponse.from(service.get(id));
    }

    @GetMapping
    public List<UserResponse> searchByName(@RequestParam(name = "name") String name) {
        return service.searchByName(name).stream().map(UserResponse::from).toList();
    }

    // Endpoint de update "normal" (sem senha)
    @PutMapping("/{id}")
    public UserResponse updateProfile(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest req) {
        return UserResponse.from(service.updateProfile(id, req));
    }

    // Endpoint exclusivo de senha
    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable UUID id, @Valid @RequestBody ChangePasswordRequest req) throws BadRequestException {
        service.changePassword(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
