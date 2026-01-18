package aoki.restaurantes.api;

import aoki.restaurantes.dto.LoginRequest;
import aoki.restaurantes.dto.LoginValidationResponse;
import aoki.restaurantes.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/validate")
    public LoginValidationResponse validate(@Valid @RequestBody LoginRequest request) {
        boolean valid = authService.validate(request);
        return new LoginValidationResponse(valid);
    }
}
