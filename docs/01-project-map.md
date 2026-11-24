# Reference Sheet 01: Project Map

Come back to this when you forget "where does X live?" or "what happens when a request arrives?".

## The layers (outside to inside)

```
Client (Swagger, REST Client, a website)
   |  HTTP + JSON
   v
Security filter chain      (security/SecurityConfig)   checks the JWT, builds the user's roles
   v
Controller                 (web/*Controller)           URL -> method, validates input (@Valid)
   v
Service                    (service/*Service)          business rules, @Transactional
   v
Repository                 (repository/*Repository)    talks to the database through JPA
   v
Database                   (H2 or PostgreSQL)          tables created by Flyway migrations
```

Rule of thumb: each layer only talks to the layer directly below it. Controllers never touch repositories; services never know about HTTP.

## One request, step by step: `POST /api/products/5/movements` with `{"type":"OUT","quantity":3}`

1. **Filter chain**: reads `Authorization: Bearer <token>`, checks signature and expiry, creates the logged-in user with role `ROLE_STAFF` or `ROLE_ADMIN`.
2. **StockController.record**: Spring turns the JSON into a `StockMovementRequest` record and runs the validation annotations (`@NotNull`, `@Min(1)`). Failure -> `GlobalExceptionHandler` returns 400.
3. **StockService.record** (one transaction):
   1. `findWithLockById(5)` loads product 5 and **locks its row**.
   2. `MovementType.OUT.apply(current, 3)` computes the new quantity.
   3. If the result is negative -> `InsufficientStockException` -> rollback -> 409.
   4. Otherwise `product.setQuantity(...)` and a `StockMovement` row is inserted.
   5. The transaction commits: product update and history row are saved together, or not at all.
4. **Controller** returns `201 Created` with the movement as JSON.

## File-by-file

| File | What it does |
|---|---|
| `InventoryApiApplication` | `main()`; starts Spring |
| `config/AppProperties` | Typed view of the `app:` block in `application.yml` |
| `config/DataSeeder` | Creates the first admin at startup |
| `config/OpenApiConfig` | Swagger title and the "Authorize" button setup |
| `domain/User, Category, Product, StockMovement` | Entities = tables as classes |
| `domain/Role, MovementType` | Enums; `MovementType` holds the stock arithmetic |
| `repository/*Repository` | Interfaces; Spring writes the SQL from method names |
| `service/AuthService` | Register and login |
| `service/CategoryService, ProductService` | CRUD rules, duplicate checks, paging |
| `service/ProductSpecifications` | Builds dynamic WHERE clauses |
| `service/StockService` | Safe stock changes (locking + transaction) |
| `security/SecurityConfig` | Which URLs are public, how tokens are verified |
| `security/JwtService` | Signs tokens |
| `web/*Controller` | URLs |
| `web/GlobalExceptionHandler` | Exceptions -> JSON errors |
| `dto/*` | JSON shapes |
| `exception/ApiExceptions` | Our own exception types |
| `tour/JavaFeatureTour` | Java refresher |
| `resources/application.yml` | Settings (H2 by default) |
| `resources/application-postgres.yml` | Settings for PostgreSQL |
| `resources/db/migration/V1, V2` | Schema and seed data |

## Why these design choices (good interview answers)

* **DTOs instead of entities in JSON**: you can change the database without breaking clients, and you never leak `password_hash`.
* **Stock changes only through movements**: the history is a complete audit trail; the product update endpoint cannot edit quantity.
* **Pessimistic row lock**: two cashiers selling the last item at the same instant are processed one after the other.
* **Flyway migrations**: the schema is code, versioned in Git, identical on every machine.
* **Stateless JWT**: no server-side session, so the API can run on several servers.
* **`ddl-auto: none`**: Hibernate never changes tables; only reviewed SQL does.
