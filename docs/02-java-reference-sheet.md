# Reference Sheet 02: Java Refresher

The runnable version of this sheet is `src/main/java/.../tour/JavaFeatureTour.java` (12 numbered lessons).
Open it and click Run above `main()`. Below is the quick lookup, with where each idea is used in the project.

## Types and variables

```java
int n = 7;  long big = 3_000_000_000L;  double d = 3.5;  boolean ok = true;  char c = 'A';
String s = "text";      var list = new ArrayList<String>();    // var = inferred type
BigDecimal price = new BigDecimal("19.99");                    // money: NEVER double (Product.price)
Integer boxed = n;                                             // autoboxing: int <-> Integer
```

* `/` on two ints is integer division (`7 / 2 == 3`). Use `7 / 2.0` for `3.5`.
* `==` compares references for objects; use `.equals()` for Strings, `.compareTo()` for BigDecimal.

## Control flow

```java
if (a) {...} else if (b) {...} else {...}
for (int i = 0; i < 5; i++) {...}      for (String x : list) {...}      while (cond) {...}
String r = switch (day) { case SATURDAY, SUNDAY -> "weekend"; default -> "weekday"; };   // switch expression
```

## Classes

```java
public class Product {                      // entity: fields private, getters/setters public
    private Long id;                        // private = only this class
    protected Product() {}                  // protected constructor required by JPA
    public Product(String sku) {...}        // constructor
    @Override public String toString() {...}
}
record ProductRequest(String sku, int quantity) {}   // immutable data carrier: constructor, getters, equals, toString for free
enum Role { ADMIN, STAFF }                           // fixed set of values; can have fields and methods (MovementType)
interface Discount { BigDecimal apply(BigDecimal p); default ...; static ...; }
abstract class Account { abstract BigDecimal monthlyFee(); }
```

* Inheritance: `class Premium extends Account`; interfaces: `implements`.
* Access: `private` < (package) < `protected` < `public`.
* `static` belongs to the class, not to an object. `final` cannot change after assignment.

## Generics, lambdas, streams

```java
List<String> names = new ArrayList<>();                      // generic type
<T extends Comparable<T>> T maxOf(List<T> list)              // generic method (JavaFeatureTour #5)
Function<Integer,Integer> twice = x -> x * 2;                // lambda
list.stream().filter(p -> p.isLowStock()).map(ProductResponse::from).toList();   // stream pipeline (ProductService)
Collectors.groupingBy(Item::category, Collectors.counting()) // group and count (JavaFeatureTour #7)
Optional<User> u = repo.findByUsername("x");  u.map(...).orElseThrow(() -> new NotFoundException("..."));
```

Stream recipe: **source** (`list.stream()`) -> **intermediate** steps (`filter`, `map`, `sorted`) -> **terminal** step (`toList`, `collect`, `count`, `sum`, `reduce`).

## Collections cheat table

| Need | Use |
|---|---|
| Ordered list, index access | `ArrayList` |
| No duplicates | `HashSet` (fast), `TreeSet` (sorted), `LinkedHashSet` (insertion order) |
| Key -> value | `HashMap`, `TreeMap` (sorted), `LinkedHashMap` (insertion order), `EnumMap` |
| Stack / queue | `ArrayDeque` |
| Smallest-first | `PriorityQueue` |
| Immutable | `List.of(...)`, `Set.of(...)`, `Map.of(...)` |
| Thread-safe map | `ConcurrentHashMap` |

## Exceptions

```java
try { ... } catch (IllegalArgumentException e) { ... } finally { ... }
try (Resource r = new Resource()) { ... }      // r.close() is called automatically
throw new ConflictException("duplicate");      // unchecked: our ApiExceptions extend RuntimeException
```

* **Checked** exceptions (extend `Exception`) must be caught or declared with `throws`. **Unchecked** (extend `RuntimeException`) need not.
* In multi-catch `catch (A | B e)`, A and B must not be parent and child.

## Concurrency

* `ExecutorService` runs tasks on a thread pool; `Future.get()` waits for a result; `CompletableFuture` chains async steps.
* `AtomicInteger`, `ConcurrentHashMap.merge` are thread-safe without `synchronized`.
* In this project, concurrency safety for stock comes from the **database row lock** (`findWithLockById`), not from Java locks, because the API may run on several servers.

## Time

```java
LocalDate due = LocalDate.of(2026, 11, 23).plusDays(30);
Duration.ofMinutes(90);       Instant.now();     ChronoUnit.DAYS.between(a, b);
```
Store timestamps as `Instant` (UTC); convert to a local time zone only when showing them to people.

## Strings

```java
"a,b".split(",");   s.trim();   s.isBlank();   s.toLowerCase(Locale.ROOT);
"%s has %d items".formatted(name, count);      // formatting
String block = """ text blocks """;           // multi-line
StringBuilder sb = new StringBuilder(); sb.append("x");   // build strings in loops
```

## Naming conventions

`ClassName`, `methodName`, `variableName`, `CONSTANT_NAME`, `package.names.lowercase`. One public class per file, file name = class name.
