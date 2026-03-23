# Restaurantes – Tech Challenge Fase 2

Backend da Fase 2 do Tech Challenge, expandindo o sistema com **Tipos de Usuário**, **Restaurantes** e **Itens de Cardápio**, além da manutenção do módulo de **Usuários** e da **validação de login**.
---

## Stack
- Java 21 • Spring Boot • Spring Data JPA
- PostgreSQL
- Docker + Docker Compose
- Swagger/OpenAPI
- Postman
- JUnit 5 • Mockito • MockMvc
- JaCoCo

---

## 🏗️ Arquitetura em camadas

A aplicação segue uma arquitetura em camadas para separar responsabilidades e facilitar manutenção, testes e evolução do sistema:

- **API (Controllers)**: expõe endpoints REST, recebe requisições HTTP, valida entrada com `@Valid` e retorna DTOs.
- **Service (Regras de negócio)**: concentra as regras da aplicação, como unicidade de e-mail, associação de usuário ao tipo, validação do dono do restaurante e fluxo de troca de senha.
- **Repository (Persistência)**: acesso ao banco via Spring Data JPA, com operações CRUD e consultas derivadas.
- **Domain (Modelo)**: entidades JPA e objetos do domínio, como `User`, `UserType`, `Restaurant`, `MenuItem` e `Address`.
- **DTO (Contrato da API)**: objetos de request/response usados na comunicação com a API.
- **Exception (Erros padronizados)**: exceções de negócio e handler global utilizando `ProblemDetail` (RFC 7807).
- **Shared (Constantes utilitárias)**: mensagens e constantes reutilizáveis.

---

## 📦 Organização de pacotes

Estrutura sugerida do projeto:

```txt
## 📦 Organização de pacotes

Estrutura do projeto:

aoki.restaurantes
  api/
    UserController.java
    AuthController.java
    UserTypeController.java
    RestaurantController.java
    MenuItemController.java
  service/
    UserService.java
    AuthService.java
    UserTypeService.java
    RestaurantService.java
    MenuItemService.java
  repository/
    UserRepository.java
    UserTypeRepository.java
    RestaurantRepository.java
    MenuItemRepository.java
  domain/
    User.java
    UserType.java
    Restaurant.java
    MenuItem.java
    Address.java
  dto/
    UserCreateRequest.java
    UserUpdateRequest.java
    ChangePasswordRequest.java
    LoginRequest.java
    LoginValidationResponse.java
    UserResponse.java
    UserTypeCreateRequest.java
    UserTypeUpdateRequest.java
    UserTypeResponse.java
    RestaurantCreateRequest.java
    RestaurantUpdateRequest.java
    RestaurantResponse.java
    MenuItemCreateRequest.java
    MenuItemUpdateRequest.java
    MenuItemResponse.java
    AddressDto.java
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



## Funcionalidades implementadas
### **Tipos de Usuário**</br>
- CRUD completo de tipos de usuário </br>
- Associação do usuário a um tipo existente </br>
- Controle de duplicidade no nome do tipo </br>
### Usuários
- CRUD de usuários </br>
- Busca por nome</br>
- E-mail único</br>
- Atualização de perfil em endpoint separado</br>
- Troca de senha em endpoint separado</br>
- Validação de login consultando o banco</br>
### Restaurantes
- CRUD completo de restaurantes</br>
- Associação do restaurante a um usuário existente</br>
- Validação para garantir que o responsável seja do tipo RESTAURANT_OWNER.</br>
### Itens do Cardápio
- CRUD completo de itens vinculados a um restaurante</br>
- Campos de nome, descrição, preço, disponibilidade apenas no local e caminho da foto</br>
## 🧱Modelagem resumida

### UserType

Representa os tipos de usuário do sistema.</br>
Exemplos:

- CLIENT</br>
- RESTAURANT_OWNER</br>
### User
Representa o usuário do sistema, contendo:

- dados cadastrais</br>
- tipo de usuário</br>
- endereço</br>
- datas de criação e última alteração</br>
### Restaurant</br>

Representa o restaurante cadastrado, contendo:

- nome</br>
- endereço</br>
- tipo de cozinha</br>
- horário de funcionamento</br>
- dono do restaurante (ownerUserId)</br>

### MenuItem

Representa um item do cardápio de um restaurante, contendo:

- nome</br>
- descrição</br>
- preço</br>
- disponibilidade apenas no local</br>
- caminho da foto</br>
- vínculo com restaurante</br>

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
- `SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver`
- `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
- `SPRING_JPA_OPEN_IN_VIEW=false`
- `SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect`


---

## 🧑‍💻 Como rodar pela IDE (loca)
1- Subir apenas o banco 
>docker compose up -d db

2- application.properties (local)
````
properties
spring.application.name=restaurantes

# App
server.port=8080
server.servlet.context-path=/api

# Datasource (PostgreSQL local)
spring.datasource.url=jdbc:postgresql://localhost:5433/restaurantes
spring.datasource.username=restaurantes
spring.datasource.password=restaurantes
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
````
Depois só da <b><b>Run</b> na aplicação
## 🔗 Endpoints

Os paths abaixo representam os mappings dos controllers.
Se o projeto estiver com ``context-path=/api``, a URL externa fica com ``/api`` antes do path

### Tipos de Usuário
- **POST** `/api/v1/user-types` — criar tipo
- **GET** `/api/v1/user-types` — listar tipos
- **GET** `/api/v1/user-types/{id}` — buscar por id
- **PUT** `/api/v1/user-types/{id}` — atualizar tipo
- **DELETE** `/api/v1/user-types/{id}` — remover tipo

### Usuários 
- **POST** `/api/v1/users` — criar usuário
- **GET** `/api/v1/users/{id}` — buscar por id
- **GET** `/api/v1/users?name=...` — buscar por nome
- **PUT** `/api/v1/users/{id}` — atualizar perfil
- **PATCH** `/api/v1/users/{id}/password` — trocar senha
- **DELETE** `/api/v1/users/{id}` — remover usuário



### Auth
- **POST** `/api/v1/auth/validate` — validar login/senha


### Restaurantes
- **POST** `/api/v1/restaurants` — criar restaurante
- **GET** `/api/v1/restaurants` — listar restaurantes
- **GET** `/api/v1/restaurants/{id}` — buscar restaurante por id
- **PUT** `/api/v1/restaurants/{id}` — atualizar restaurante
- **DELETE** `/api/v1/restaurants/{id}` — remover restaurante
### Itens do Cardápio
- **POST** `/api/v1/restaurants/{restaurantId}/menu-items` — criar item
- **GET** `/api/v1/restaurants/{restaurantId}/menu-items` — listar itens do restaurante
- **GET** `/api/v1/restaurants/{restaurantId}/menu-items/{id}` — buscar item por id
- **PUT** `/api/v1/restaurants/{restaurantId}/menu-items/{id}` — atualizar item
- **DELETE** `/api/v1/restaurants/{restaurantId}/menu-items/{id}` — remover item



---

## ⚡ Exemplos rápidos
### Criar tipo de usuário
```json
{
  "name": "Client"
}
````
### Criar usuário
```json
{
  "name": "Fernanda",
  "email": "fer@example.com",
  "login": "fer",
  "password": "123456",
  "userTypeId": "11111111-1111-1111-1111-111111111111",
  "address": {
    "street": "Rua A",
    "number": "10",
    "city": "Rio de Janeiro",
    "zipcode": "20000-000",
    "complement": "Apto 101"
  }
}

````
### Criar restaurante 
````
{
  "name": "Restaurante do João",
  "address": {
    "street": "Rua B",
    "number": "20",
    "city": "Niterói",
    "zipcode": "24000-000",
    "complement": "Loja"
  },
  "cuisineType": "Brasileira",
  "openingHours": "08:00 às 22:00",
  "ownerUserId": "22222222-2222-2222-2222-222222222222"
}

````

### Criar item do cardápio
````
{
  "name": "Feijoada",
  "description": "Feijoada completa",
  "price": 49.90,
  "dineInOnly": false,
  "photoPath": "/images/feijoada.jpg"
}
````

### Validar Login
````
"login": "fer", 
"password": "123456"
} 
````
### Resposta
````
{
"Valid": true
}
````
## ❗ Tratamento de erros (ProblemDetail – RFC 7807)

A API padroniza erros com ProblemDetail:

- type: /problems/<tipo>
- title
- status
- detail
- timestamp

Em validações, a resposta também pode incluir errors com lista de campos e mensagens.

Exemplo:

````
{
  "type": "/problems/conflict",
  "title": "Conflict",
  "status": 409,
  "detail": "E-mail ja cadastrado.",
  "timestamp": "2026-03-21T00:00:00Z"
}

````
## 🧪 Testes automatizados

O projeto possui:

### Testes unitários
- UserTypeServiceTest</br>
- UserServiceTest</br>
- RestaurantServiceTest</br>
- MenuItemServiceTest</br>
###  Testes de integração</br>
- UserTypeIntegrationTest</br>
- UserIntegrationTest</br>
- RestaurantIntegrationTest</br>
- MenuItemIntegrationTest</br>
- AuthIntegrationTest</br>
### Cobertura
94% de cobertura total com JaCoCo

Para rodar os testes:
````
mvn clean test
````
Para gerar o relatório de coberturas 
````
mvn clean test
````

Relatório JaCoCo
````
target/site/jacoco/index.html
````
## 🧪 Postman 

Coleção e environment (sugestão de pasta: `postman/`):
- `postman/Restaurantes_Fase2_API_Completa.postman_collection.json`
- `postman/Restaurantes_Fase2_Local.postman_environment.json`

A Coleção cobre

- CRUD de tipos de usuário
- CRUD de usuários
- validação de login
- CRUD de restaurantes
- CRUD de itens do cardápio
- cenários principais de erro
---


## ✅ Requisitos atendidos 

- CRUD de tipos de usuário
-  Associação de usuário a tipo de usuário
- CRUD de usuários
-  CRUD de restaurantes
-  CRUD de itens do cardápio
-  Validação de login consultando o banco
-  Versionamento de API
-  Erros padronizados com ProblemDetail
-  Docker Compose
-  Swagger/OpenAPI
-  Coleção Postman
-  Testes unitários
-  Testes de integração
-  Cobertura superior a 80% (93%)
-  Organização em camadas, com separação de responsabilidades

### 📄 Observação final

Este projeto foi evoluído a partir da Fase 1, mantendo o módulo de Usuários e expandindo o domínio com Tipos de Usuário, Restaurantes e Cardápio, além de reforçar a qualidade do código com testes automatizados e cobertura de testes.</br>
Embora o projeto nao esteja separado literalmente em pastas application e infrastructure, a estrutura atual segue os principios centrais de separacao de responsabilidades esperados para uma arquitetura limpa em uma API Spring Boot.