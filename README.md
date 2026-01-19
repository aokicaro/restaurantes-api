# Restaurantes – Tech Challenge Fase 1

Backend da Fase 1 do Tech Challenge (módulo de **Usuários**): CRUD, busca por nome, e-mail único, troca de senha em endpoint separado e validação de login consultando o banco.

---

## Stack
- Java 21 • Spring Boot • Spring Data JPA
- PostgreSQL
- Docker + Docker Compose
- Swagger/OpenAPI
- Postman

---

## 🏗️ Arquitetura em camadas

A aplicação segue uma arquitetura em camadas para separar responsabilidades e facilitar manutenção/testes:

- **API (Controllers)**: expõe endpoints REST, valida entrada (`@Valid`) e retorna DTOs.
- **Service (Regras de negócio)**: concentra validações e fluxos (e-mail único, update de perfil, troca de senha, validação de login).
- **Repository (Persistência)**: acesso ao banco via Spring Data JPA (queries e operações CRUD).
- **Domain (Modelo)**: entidades JPA e objetos do domínio (`User`, `Address`, `UserRole`).
- **DTO (Contrato da API)**: modelos de request/response usados nos endpoints.
- **Exception (Erros padronizados)**: exceções de negócio e handler global com `ProblemDetail` (RFC 7807).
- **Shared (Constantes utilitárias)**: mensagens padronizadas (ex.: enum `Messages`).

---

## 📦 Organização de pacotes

Estrutura sugerida do projeto:

```txt
aoki.restaurantes
  api/
    UserController.java
    AuthController.java
  service/
    UserService.java
    AuthService.java
  repository/
    UserRepository.java
  domain/
    User.java
    Address.java
    UserRole.java
  dto/
    UserCreateRequest.java
    UserUpdateRequest.java
    ChangePasswordRequest.java
    LoginRequest.java
    LoginValidationResponse.java
    UserResponse.java
  exception/
    ApiExceptionHandler.java
    ConflictException.java
    NotFoundException.java
    BadRequestException.java
  shared/
    Messages.java

````
### Descrição rápida dos pacotes

- **api**: controllers REST (entrada HTTP/saída DTO).
- **service**: regras de negócio e transações.
- **repository**: consultas e persistência (JPA).
- **domain**: entidades e tipos do domínio.
- **dto**: contratos da API (requests/responses).
- **exception**: exceções + handler com `ProblemDetail`.
- **shared**: mensagens e constantes reutilizáveis.


## Como rodar (Docker Compose)

### Subir API + banco
Na raiz do projeto:

```bash
docker compose up --build 
```
## 🔌 Acessos

> A aplicação usa `server.servlet.context-path=/api`, então a base é `/api`.

- **API (base):** `http://localhost:8080/api`
- **Swagger UI:** `http://localhost:8080/api/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8080/api/v3/api-docs`
- **PostgreSQL (host):** `localhost:5433`

---

## 🧩 Variáveis de ambiente (Docker)

### Banco (`db`)
- `POSTGRES_DB=restaurantes`
- `POSTGRES_USER=restaurantes`
- `POSTGRES_PASSWORD=restaurantes`
- `5433:5432`

### Aplicação (`app`)
- `SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/restaurantes`
- `SPRING_DATASOURCE_USERNAME=restaurantes`
- `SPRING_DATASOURCE_PASSWORD=restaurantes`
- `SPRING_JPA_HIBERNATE_DDL_AUTO=update`

---

## 🔗 Endpoints (base: /api/api/v1)

> `context-path=/api` + rotas `/api/v1` ⇒ `/api/api/v1`

### Usuários
- **POST** `/api/api/v1/users` — criar
- **GET** `/api/api/v1/users/{id}` — buscar por id
- **GET** `/api/api/v1/users?name=...` — buscar por nome
- **PUT** `/api/api/v1/users/{id}` — atualizar perfil (sem senha)
- **PATCH** `/api/api/v1/users/{id}/password` — trocar senha
- **DELETE** `/api/api/v1/users/{id}` — remover

### Auth
- **POST** `/api/api/v1/auth/validate` — validar login/senha

---

## ⚡ Exemplos rápidos

### Criar usuário
```json
{
  "name": "Fernanda Aoki",
  "email": "fer.aoki@example.com",
  "login": "fernandaAoki",
  "password": "123456",
  "role": "CLIENTE",
  "address": {
    "street": "Rua A",
    "number": "10",
    "city": "Rio de Janeiro",
    "zipcode": "20000-000",
    "complement": "Apto 101"
  }
}
````

### Validar login
```json
{
  "login": "fernandaAoki",
  "password": "123456"
}
```
### Resposta
```json
{ "valid": true }

````
## 🧪 Postman

Coleção e environment (sugestão de pasta: `postman/`):
- `postman/Restaurantes_Usuarios_APIv1.postman_collection.json`
- `postman/Restaurantes_Local.postman_environment.json`

---

## ✅ Requisitos atendidos 

- CRUD de usuários
- Endpoint separado para troca de senha
- Endpoint separado para atualização de perfil
- Registro de última alteração (`lastModified`)
- Busca por nome
- E-mail único
- Validação de login consultando o banco
- Versionamento de API (`/api/v1`)
- Erros padronizados com `ProblemDetail` (RFC 7807)
- PostgreSQL + Docker Compose
- Swagger/OpenAPI + coleção Postman

