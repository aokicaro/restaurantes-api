package aoki.restaurantes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        return base(HttpStatus.NOT_FOUND, "Nao encontrado", ex.getMessage(), "not-found");
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        return base(HttpStatus.CONFLICT, "Conflito", ex.getMessage(), "conflict");
    }

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException ex) {
        return base(HttpStatus.BAD_REQUEST, "Requisicao invalida", ex.getMessage(), "bad-request");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = base(HttpStatus.BAD_REQUEST, "Validacao", "Campos invalidos.", "validation");
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .toList();
        pd.setProperty("errors", errors);
        return pd;
    }

    private ProblemDetail base(HttpStatus status, String title, String detail, String typeSuffix) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create("https://api.suaapp.com/problems/" + typeSuffix));
        pd.setProperty("timestamp", Instant.now().toString());
        return pd;
    }
}
