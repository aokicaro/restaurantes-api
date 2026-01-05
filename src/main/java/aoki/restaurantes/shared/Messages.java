package aoki.restaurantes.shared;

public class Messages {
    private Messages(){}
    public static final class User {
        private User() {}
        public static final String EMAIL_ALREADY_EXISTS = "E-mail ja cadastrado.";
        public static final String NOT_FOUND = "Usuario nao encontrado.";
        public static final String INVALID_CURRENT_PASSWORD = "Senha atual invalida.";
}}
