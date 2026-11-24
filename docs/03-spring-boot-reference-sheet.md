# Reference Sheet 03: Spring Boot, JPA and Security

## Core idea: Dependency Injection (DI)

Spring creates one object ("bean") per `@Component`-like class and passes them into constructors:

```java
@Service
public class ProductService {
    private final ProductRepository products;                 // Spring provides this
    public ProductService(ProductRepository products) { this.products = products; }
}
```

## Annotations you will meet

| Annotation | Where | Meaning |
|---|---|---|
| `@SpringBootApplication` | main class | Start-up: scan, auto-configure |
| `@Component` / `@Service` / `@Repository` | classes | "Create a bean of this class" (Service = business logic) |
| `@Configuration` + `@Bean` | config classes | Methods that build beans by hand (`SecurityConfig`) |
| `@RestController` | controllers | Methods return JSON |
| `@RequestMapping("/api/x")` | class | URL prefix |
| `@GetMapping @PostMapping @PutMapping @DeleteMapping` | methods | HTTP method + path |
| `@PathVariable` | parameter | `/products/{id}` -> `Long id` |
| `@RequestParam` | parameter | `?page=2` -> `int page` (can have `defaultValue`) |
| `@RequestBody` | parameter | JSON body -> object |
| `@Valid` | parameter | Run validation annotations first |
| `@RestControllerAdvice` + `@ExceptionHandler` | class/method | Global error handling |
| `@Transactional` | service methods | All-or-nothing database work |
| `@PreAuthorize("hasRole('ADMIN')")` | controller methods | Permission check |
| `@ConfigurationProperties` | record/class | Binds `application.yml` values |
| `@Value("${x}")` | field | Inject a single property (we use records instead) |

## HTTP status codes used

| Code | When |
|---|---|
| 200 OK | Read or update succeeded |
| 201 Created | New resource created (+ `Location` header) |
| 204 No Content | Delete succeeded |
| 400 Bad Request | Validation failed, bad sort field |
| 401 Unauthorized | No/invalid token, wrong password |
| 403 Forbidden | Logged in but role not allowed |
| 404 Not Found | Unknown id |
| 409 Conflict | Duplicate SKU, not enough stock, category in use |

## JPA (Java Persistence API) mapping

```java
@Entity @Table(name = "products")             // class <-> table
@Id @GeneratedValue(strategy = IDENTITY)      // primary key made by the database
@Column(name = "reorder_level", nullable = false, length = 40)
@Enumerated(EnumType.STRING)                  // store enum names, not numbers
@ManyToOne(fetch = FetchType.LAZY)            // many products -> one category; load on demand
@JoinColumn(name = "category_id")             // the foreign key column
@PrePersist / @PreUpdate                      // run before INSERT / UPDATE
```

* **Dirty checking**: inside a transaction, change an entity with a setter and Hibernate writes the UPDATE on commit. No `save()` needed.
* **Lazy loading** only works inside a transaction. We set `open-in-view: false`, so services convert entities to DTOs before the transaction ends.
* **N+1 problem**: loading 20 products then 20 separate category queries. Fixed with `join fetch` (`findLowStock`) and `default_batch_fetch_size`.

## Repository method names become SQL

| Method | SQL idea |
|---|---|
| `findByUsername(String u)` | `WHERE username = ?` |
| `existsBySku(String sku)` | `SELECT 1 ... WHERE sku = ?` |
| `existsByNameIgnoreCaseAndIdNot(n, id)` | `WHERE lower(name) = lower(?) AND id <> ?` |
| `findAllByOrderByNameAsc()` | `ORDER BY name ASC` |
| `findByProductIdOrderByCreatedAtDesc...(id, Pageable)` | `WHERE product_id = ? ORDER BY ... LIMIT ? OFFSET ?` + count query |

For anything else: `@Query("jpql")` or a `Specification` (see `ProductSpecifications`).

## Validation annotations (jakarta.validation)

`@NotNull` (not null), `@NotBlank` (not null/empty/spaces), `@Size(min,max)`, `@Min`, `@Max`, `@DecimalMin`, `@Digits`, `@Email`, `@Pattern(regexp)`. Put them on record components; add `@Valid` on the controller parameter.

## Transactions

* `@Transactional` on a service method: begin -> run -> commit. A `RuntimeException` triggers rollback.
* `readOnly = true` for pure reads.
* Calling a `@Transactional` method from another method **in the same class** bypasses the proxy and the annotation does nothing. Call it from another bean.

## Security flow (JWT)

1. `POST /api/auth/login`: `AuthService` finds the user, `BCryptPasswordEncoder.matches` compares password to hash.
2. `JwtService.issue` signs a token with claims `sub` (username), `roles`, `iat`, `exp` using HS256 and the secret.
3. Client sends `Authorization: Bearer <token>`.
4. `oauth2ResourceServer().jwt()` verifies signature and expiry, `JwtAuthenticationConverter` maps `roles: ["ADMIN"]` to `ROLE_ADMIN`.
5. `@PreAuthorize("hasRole('ADMIN')")` allows or denies.

Remember: a JWT is **signed, not encrypted**. Anyone can read its payload. Never put secrets in it.

## Configuration and profiles

* `application.yml` = defaults. `application-postgres.yml` is added when `SPRING_PROFILES_ACTIVE=postgres`.
* `${NAME:default}` reads environment variable `NAME`, or uses `default`.
* Order of power: command line > environment variables > `application-<profile>.yml` > `application.yml`.

## Testing annotations

| Annotation | Use |
|---|---|
| `@Test` | A test method |
| `@ParameterizedTest` + `@CsvSource` | One test, many data rows |
| `@ExtendWith(MockitoExtension.class)` + `@Mock` | Unit test with fake dependencies |
| `when(mock.method()).thenReturn(x)` | Program a mock |
| `verify(mock, never()).save(any())` | Check a call did (not) happen |
| `@SpringBootTest` | Start the whole app for an integration test |
| `MockMvc` | Send fake HTTP requests without a network port |
| AssertJ `assertThat(x).isEqualTo(y)` | Readable assertions |
