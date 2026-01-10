package aoki.restaurantes.shared;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Messages {
    private Messages(){}
    @Getter
    @RequiredArgsConstructor
    public enum User {
        EMAIL_ALREADY_EXISTS("E-mail ja cadastrado."),
        NOT_FOUND("Usuario nao encontrado."),
        INVALID_CURRENT_PASSWORD("Senha atual invalida.");

        private final String text;
}}
