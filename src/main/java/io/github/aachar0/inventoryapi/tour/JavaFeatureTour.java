package io.github.aachar0.inventoryapi.tour;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * JAVA REFRESHER IN ONE FILE (Java 21).
 * Read it top to bottom: each numbered section is a small, runnable lesson and returns one line of output.
 * Run it: open this file in VS Code and click "Run" above main(), or run the test JavaFeatureTourTest.
 * The test JavaFeatureTourTest runs every section, so if you break a lesson the build tells you.
 *
 * Sections: 1 variables/operators, 2 control flow, 3 switch expressions, 4 sealed types + records + pattern matching,
 * 5 generics + lambdas, 6 OOP (abstract class, inheritance, polymorphism, interface default/static methods, enums),
 * 7 streams, 8 collections, 9 exceptions + try-with-resources, 10 concurrency, 11 time + BigDecimal,
 * 12 text blocks, varargs, Optional, instanceof patterns, custom Iterable, inner classes.
 */
public final class JavaFeatureTour {

    private JavaFeatureTour() {
    }

    public static void main(String[] args) throws Exception {
        run().forEach(System.out::println);
    }

    /** Runs every lesson and returns the output lines (returning them, instead of printing, makes them testable). */
    public static List<String> run() throws InterruptedException, ExecutionException {
        List<String> out = new ArrayList<>();
        out.add("1. " + basics());
        out.add("2. " + controlFlow());
        out.add("3. " + switchExpressions());
        out.add("4. " + sealedAndRecords());
        out.add("5. " + genericsAndLambdas());
        out.add("6. " + objectOrientation());
        out.add("7. " + streams());
        out.add("8. " + collections());
        out.add("9. " + exceptions());
        out.add("10. " + concurrency());
        out.add("11. " + timeAndMoney());
        out.add("12. " + misc());
        return out;
    }

    // ------------------------------------------------------------------------------------------
    // 1. VARIABLES, TYPES, OPERATORS
    // ------------------------------------------------------------------------------------------
    private static String basics() {
        int count = 7;                          // 32-bit whole number
        long big = 3_000_000_000L;              // 64-bit; underscores make big numbers readable; L marks a long
        double ratio = count / 2.0;             // double arithmetic: 3.5 (count / 2 would be integer division = 3)
        char grade = 'A';                       // one UTF-16 character
        boolean ok = count > 5 && !(big < 0);   // && and || short-circuit; ! negates
        var list = new ArrayList<String>();     // 'var': the compiler infers ArrayList<String>
        list.add("x");
        int bits = (count << 2) | 1;            // << shift left, | bitwise OR (also & AND, ^ XOR, ~ NOT, >> >>>)
        long rem = big % 7;                     // % is the remainder
        String kind = count % 2 == 0 ? "even" : "odd";   // ternary: condition ? a : b
        int narrowed = (int) 3.99;              // cast: truncates to 3 (no rounding)
        Integer boxed = count;                  // autoboxing: int <-> Integer
        return "count=%d big=%d ratio=%.1f grade=%c ok=%b bits=%d rem=%d kind=%s narrowed=%d boxed=%d list=%s"
                .formatted(count, big, ratio, grade, ok, bits, rem, kind, narrowed, boxed, list);
    }

    // ------------------------------------------------------------------------------------------
    // 2. CONTROL FLOW: for, while, do-while, labels, break, continue
    // ------------------------------------------------------------------------------------------
    private static String controlFlow() {
        StringBuilder sb = new StringBuilder();
        outer:                                          // a label lets break/continue target the OUTER loop
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                if (j > i) {
                    continue outer;                     // jump to the next i
                }
                if (i * j > 12) {
                    break outer;                        // leave both loops
                }
                sb.append(i * j).append(' ');
            }
        }
        int w = 0;
        while (w < 3) {                                 // test first
            w++;
        }
        do {                                            // body runs at least once, test after
            w--;
        } while (w > 1);
        int total = 0;
        for (int n : new int[]{1, 2, 3, 4}) {           // enhanced for: iterate arrays and Iterables
            total += n;
        }
        return sb.toString().trim() + " | w=" + w + " total=" + total;
    }

    // ------------------------------------------------------------------------------------------
    // 3. SWITCH EXPRESSIONS (arrow form returns a value; no fall-through)
    // ------------------------------------------------------------------------------------------
    private static String switchExpressions() {
        DayOfWeek day = LocalDate.of(2026, 11, 23).getDayOfWeek();   // enum from java.time
        String type = switch (day) {
            case SATURDAY, SUNDAY -> "weekend";                      // several labels in one case
            default -> {                                             // a block uses 'yield' to return
                String base = "week";
                yield base + "day";
            }
        };
        int len = switch ("hello") {                                 // switch on a String
            case "hi" -> 2;
            case "hello" -> 5;
            default -> 0;
        };
        return day + " is a " + type + ", len=" + len;
    }

    // ------------------------------------------------------------------------------------------
    // 4. SEALED INTERFACE + RECORDS + PATTERN MATCHING
    // ------------------------------------------------------------------------------------------
    /** 'sealed' = only the listed types may implement it, so the compiler can check a switch is complete. */
    sealed interface Shape permits Circle, Rect, Square {
    }

    record Circle(double radius) implements Shape {
    }

    record Rect(double width, double height) implements Shape {
    }

    record Square(double side) implements Shape {
        Square {                                                     // compact constructor: validate inputs
            if (side < 0) {
                throw new IllegalArgumentException("side must be >= 0");
            }
        }
    }

    static double area(Shape shape) {
        return switch (shape) {                                      // no default needed: Shape is sealed
            case Circle c -> Math.PI * c.radius() * c.radius();      // type pattern
            case Rect(double w, double h) -> w * h;                  // record pattern: unpacks the fields
            case Square s when s.side() > 100 -> -1;                 // 'when' = guard condition
            case Square s -> s.side() * s.side();
        };
    }

    private static String sealedAndRecords() {
        List<Shape> shapes = List.of(new Circle(1), new Rect(2, 3), new Square(4), new Square(500));
        String areas = shapes.stream().map(s -> "%.2f".formatted(area(s))).collect(Collectors.joining(", "));
        Rect r = new Rect(2, 3);
        return "areas=[" + areas + "] record=" + r + " equal=" + r.equals(new Rect(2, 3));
    }

    // ------------------------------------------------------------------------------------------
    // 5. GENERICS AND LAMBDAS
    // ------------------------------------------------------------------------------------------
    /** Generic method: T must be comparable with itself, so we can find the maximum of any such list. */
    static <T extends Comparable<T>> T maxOf(List<T> items) {
        return items.stream().max(Comparator.naturalOrder()).orElseThrow();
    }

    /** Wildcard: accepts a collection of Integer, Double, Long ... (anything that extends Number). */
    static double total(java.util.Collection<? extends Number> numbers) {
        double sum = 0;
        for (Number n : numbers) {
            sum += n.doubleValue();
        }
        return sum;
    }

    /** A generic class: Box<String>, Box<Integer>, ... */
    static class Box<T> {
        private final T value;

        Box(T value) {
            this.value = value;
        }

        <R> Box<R> map(Function<T, R> fn) {          // generic method inside a generic class
            return new Box<>(fn.apply(value));
        }

        T get() {
            return value;
        }
    }

    /** Your own functional interface (exactly one abstract method) can be written as a lambda. */
    @FunctionalInterface
    interface Discount {
        BigDecimal apply(BigDecimal price);

        default Discount then(Discount next) {       // default method: behaviour inside an interface
            return price -> next.apply(apply(price));
        }

        static Discount percent(int pct) {           // static method on an interface
            return price -> price.multiply(BigDecimal.valueOf(100 - pct))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
    }

    private static String genericsAndLambdas() {
        Function<Integer, Integer> twice = x -> x * 2;
        BiFunction<Integer, Integer, Integer> add = Integer::sum;     // method reference
        Supplier<List<String>> newList = ArrayList::new;              // constructor reference
        Predicate<String> isEmpty = String::isEmpty;
        UnaryOperator<String> shout = String::toUpperCase;
        int composed = twice.andThen(x -> x + 1).apply(5);            // (5*2)+1
        Discount tenThenFive = Discount.percent(10).then(Discount.percent(5));
        return "max=%s total=%.1f composed=%d add=%d emptyList=%s isEmpty=%b shout=%s box=%d discount=%s".formatted(
                maxOf(List.of(3, 9, 4)), total(List.of(1, 2.5, 3L)), composed, add.apply(2, 3),
                newList.get(), isEmpty.test(""), shout.apply("ok"),
                new Box<>("four").map(String::length).get(),
                tenThenFive.apply(new BigDecimal("200.00")));
    }

    // ------------------------------------------------------------------------------------------
    // 6. OBJECT-ORIENTED PROGRAMMING
    // ------------------------------------------------------------------------------------------
    /** Abstract class: cannot be created directly; forces subclasses to implement monthlyFee(). */
    abstract static class Account {
        private final String id;                                      // private: only this class can touch it
        protected BigDecimal balance = BigDecimal.ZERO;               // protected: subclasses can too

        Account(String id) {
            this.id = id;
        }

        String id() {
            return id;
        }

        abstract BigDecimal monthlyFee();

        void deposit(BigDecimal amount) {
            if (amount.signum() <= 0) {
                throw new IllegalArgumentException("deposit must be positive");
            }
            balance = balance.add(amount);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "(" + id + ", " + balance + ")";
        }
    }

    static class Basic extends Account {
        Basic(String id) {
            super(id);                                                // call the parent constructor
        }

        @Override
        BigDecimal monthlyFee() {
            return new BigDecimal("2.50");
        }
    }

    static class Premium extends Account {
        Premium(String id) {
            super(id);
        }

        @Override
        BigDecimal monthlyFee() {
            return BigDecimal.ZERO;
        }

        @Override
        void deposit(BigDecimal amount) {                             // override and extend
            super.deposit(amount.add(BigDecimal.ONE));                // Premium gives a 1.00 bonus
        }
    }

    /** Enum with fields, a constructor and a method. */
    enum Level {
        LOW(0), MEDIUM(10), HIGH(50);

        private final int threshold;

        Level(int threshold) {
            this.threshold = threshold;
        }

        static Level of(int quantity) {
            Level result = LOW;
            for (Level level : values()) {                            // values() = all constants in order
                if (quantity >= level.threshold) {
                    result = level;
                }
            }
            return result;
        }
    }

    private static String objectOrientation() {
        List<Account> accounts = List.of(new Basic("b-1"), new Premium("p-1"));
        for (Account a : accounts) {                                  // polymorphism: same call, different behaviour
            a.deposit(new BigDecimal("10.00"));
        }
        Runnable anonymous = new Runnable() {                         // anonymous inner class
            @Override
            public void run() {
                accounts.get(0).balance = accounts.get(0).balance.subtract(accounts.get(0).monthlyFee());
            }
        };
        anonymous.run();
        EnumMap<Level, Integer> counts = new EnumMap<>(Level.class);  // fast map keyed by an enum
        for (int q : new int[]{1, 12, 70, 5, 20}) {
            counts.merge(Level.of(q), 1, Integer::sum);
        }
        return accounts + " fees=" + accounts.get(0).monthlyFee() + "/" + accounts.get(1).monthlyFee() + " levels=" + counts;
    }

    // ------------------------------------------------------------------------------------------
    // 7. STREAMS
    // ------------------------------------------------------------------------------------------
    record Item(String name, String category, int qty, BigDecimal price) {
    }

    private static String streams() {
        List<Item> items = List.of(
                new Item("Pen", "Stationery", 120, new BigDecimal("0.50")),
                new Item("Notebook", "Stationery", 40, new BigDecimal("2.25")),
                new Item("Stapler", "Stationery", 5, new BigDecimal("8.00")),
                new Item("Mouse", "Electronics", 8, new BigDecimal("15.00")));

        Map<String, Long> perCategory = items.stream()               // group + count
                .collect(Collectors.groupingBy(Item::category, TreeMap::new, Collectors.counting()));
        Map<Boolean, List<String>> lowOrOk = items.stream()          // split into two lists
                .collect(Collectors.partitioningBy(i -> i.qty() < 10, Collectors.mapping(Item::name, Collectors.toList())));
        BigDecimal stockValue = items.stream()                       // map -> reduce
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.qty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String names = items.stream().map(Item::name).sorted(Comparator.reverseOrder())
                .collect(Collectors.joining(", ", "[", "]"));
        IntSummaryStatistics stats = items.stream().mapToInt(Item::qty).summaryStatistics();
        Optional<Item> priciest = items.stream().max(Comparator.comparing(Item::price));
        int sumOfSquares = IntStream.rangeClosed(1, 4).map(n -> n * n).sum();
        List<Integer> flat = java.util.stream.Stream.of(List.of(1, 2), List.of(3), List.<Integer>of())
                .flatMap(List::stream).toList();                     // flatMap: lists of lists -> one list
        return "perCategory=%s lowOrOk=%s stockValue=%s names=%s max=%d avg=%.1f priciest=%s squares=%d flat=%s".formatted(
                perCategory, lowOrOk, stockValue, names, stats.getMax(), stats.getAverage(),
                priciest.map(Item::name).orElse("none"), sumOfSquares, flat);
    }

    // ------------------------------------------------------------------------------------------
    // 8. COLLECTIONS
    // ------------------------------------------------------------------------------------------
    private static String collections() {
        List<Integer> numbers = new ArrayList<>(List.of(5, 3, 8, 1, 9, 2));
        numbers.removeIf(n -> n % 2 == 0);                           // remove while iterating, safely
        numbers.sort(Comparator.naturalOrder());
        Set<String> sorted = new TreeSet<>(Set.of("pear", "apple", "fig"));          // sorted set
        Map<String, Integer> insertionOrder = new LinkedHashMap<>();                  // remembers insertion order
        insertionOrder.put("b", 2);
        insertionOrder.put("a", 1);
        insertionOrder.putIfAbsent("a", 99);                                          // ignored: key exists
        Deque<String> stack = new ArrayDeque<>();                                     // ArrayDeque: stack AND queue
        stack.push("first");
        stack.push("second");
        String top = stack.pop();                                                     // LIFO: last in, first out
        PriorityQueue<Integer> smallestFirst = new PriorityQueue<>(List.of(7, 2, 9));
        int smallest = smallestFirst.poll();
        Map<String, List<String>> byInitial = new TreeMap<>();
        for (String w : List.of("ant", "bee", "ape", "bat")) {
            byInitial.computeIfAbsent(w.substring(0, 1), k -> new ArrayList<>()).add(w);
        }
        List<Integer> readOnly = java.util.Collections.unmodifiableList(numbers);     // a read-only view
        return "numbers=%s sorted=%s order=%s top=%s smallest=%d byInitial=%s size=%d".formatted(
                numbers, sorted, insertionOrder, top, smallest, byInitial, readOnly.size());
    }

    // ------------------------------------------------------------------------------------------
    // 9. EXCEPTIONS AND TRY-WITH-RESOURCES
    // ------------------------------------------------------------------------------------------
    /** Checked exception: callers MUST catch or declare it. */
    static class ValidationFailed extends Exception {
        ValidationFailed(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /** AutoCloseable: close() is called automatically at the end of try-with-resources, even after an error. */
    static class Resource implements AutoCloseable {
        private final List<String> log;
        private final String name;

        Resource(String name, List<String> log) {
            this.name = name;
            this.log = log;
            log.add("open-" + name);
        }

        @Override
        public void close() {
            log.add("close-" + name);
        }
    }

    private static int parsePositive(String text) throws ValidationFailed {
        try {
            int n = Integer.parseInt(text);
            if (n <= 0) {
                throw new IllegalArgumentException("not positive");
            }
            return n;
        } catch (IllegalArgumentException e) {                           // also catches NumberFormatException (its subclass)
            // Multi-catch syntax is catch (A | B e) but A and B must NOT be parent/child of each other.
            throw new ValidationFailed("bad input '" + text + "'", e);   // wrap the original error as the cause
        }
    }

    private static String exceptions() {
        List<String> log = new ArrayList<>();
        String result;
        try (Resource a = new Resource("A", log); Resource b = new Resource("B", log)) {
            result = String.valueOf(parsePositive("42"));
            parsePositive("-3");                                          // throws
            result = "unreachable";
        } catch (ValidationFailed e) {
            result = e.getMessage() + " cause=" + e.getCause().getClass().getSimpleName();
        } finally {
            log.add("finally");                                           // always runs
        }
        return result + " log=" + log;                                    // resources close in reverse order
    }

    // ------------------------------------------------------------------------------------------
    // 10. CONCURRENCY
    // ------------------------------------------------------------------------------------------
    private static String concurrency() throws InterruptedException, ExecutionException {
        AtomicInteger atomic = new AtomicInteger();                       // thread-safe counter, no locks
        ConcurrentHashMap<String, Integer> words = new ConcurrentHashMap<>();
        List<Callable<Integer>> jobs = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            final int n = i;                                              // lambdas can only capture effectively-final variables
            jobs.add(() -> {
                atomic.addAndGet(n);
                words.merge(n % 2 == 0 ? "even" : "odd", 1, Integer::sum);   // atomic read-modify-write
                return n * n;
            });
        }
        int squares = 0;
        try (ExecutorService pool = Executors.newFixedThreadPool(2)) {   // thread pool; closed automatically (Java 19+)
            for (Future<Integer> f : pool.invokeAll(jobs)) {              // run all jobs, wait for all results
                squares += f.get();
            }
        }
        CompletableFuture<Integer> a = CompletableFuture.supplyAsync(() -> 20);
        CompletableFuture<Integer> b = CompletableFuture.supplyAsync(() -> 22);
        int combined = a.thenCombine(b, Integer::sum).get();              // combine two async results
        return "atomic=%d squares=%d words=%s combined=%d".formatted(atomic.get(), squares, new TreeMap<>(words), combined);
    }

    // ------------------------------------------------------------------------------------------
    // 11. DATES AND MONEY
    // ------------------------------------------------------------------------------------------
    private static String timeAndMoney() {
        LocalDate start = LocalDate.of(2026, 11, 23);
        LocalDate due = start.plusDays(30);
        long days = ChronoUnit.DAYS.between(start, due);
        Duration meeting = Duration.ofMinutes(90);
        BigDecimal price = new BigDecimal("19.99");
        BigDecimal withVat = price.multiply(new BigDecimal("1.16")).setScale(2, RoundingMode.HALF_UP);   // 16% VAT, rounded
        boolean notEqualByScale = new BigDecimal("2.0").equals(new BigDecimal("2.00"));                 // false! scale matters
        boolean equalByValue = new BigDecimal("2.0").compareTo(new BigDecimal("2.00")) == 0;            // use compareTo
        return "due=%s days=%d meeting=%dh%02dm withVat=%s equals=%b compareTo=%b".formatted(
                due, days, meeting.toHours(), meeting.toMinutesPart(), withVat, notEqualByScale, equalByValue);
    }

    // ------------------------------------------------------------------------------------------
    // 12. TEXT BLOCKS, VARARGS, OPTIONAL, INSTANCEOF PATTERNS, ITERABLE, INNER CLASSES
    // ------------------------------------------------------------------------------------------
    static int sum(int... numbers) {                                      // varargs: sum(), sum(1), sum(1, 2, 3)
        int total = 0;
        for (int n : numbers) {
            total += n;
        }
        return total;
    }

    /** Your own Iterable works in a for-each loop. */
    static class Countdown implements Iterable<Integer> {
        private final int from;

        Countdown(int from) {
            this.from = from;
        }

        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<>() {                                     // anonymous class with diamond
                private int next = from;

                @Override
                public boolean hasNext() {
                    return next > 0;
                }

                @Override
                public Integer next() {
                    if (next <= 0) {
                        throw new NoSuchElementException();
                    }
                    return next--;
                }
            };
        }
    }

    private static String describe(Object o) {
        if (o instanceof String s && !s.isBlank()) {                      // instanceof pattern: s is already cast
            return "text(" + s.length() + ")";
        } else if (o instanceof Integer i) {
            return "int(" + (i + 1) + ")";
        }
        return "other";
    }

    private static String misc() {
        String block = """
                Hello,
                  text block: line breaks and "quotes" need no escaping.
                """;
        Map<String, String> config = Map.of("mode", "fast");
        String mode = Optional.ofNullable(config.get("mode")).map(String::toUpperCase).orElse("none");   // no null checks
        String missing = Optional.ofNullable(config.get("color")).orElse("none");
        StringBuilder countdown = new StringBuilder();
        for (int n : new Countdown(3)) {
            countdown.append(n);
        }
        char[] letters = "java".toCharArray();
        java.util.Arrays.sort(letters);
        int[][] grid = {{1, 2}, {3, 4}};                                  // 2-D array
        return "sum=%d lines=%d mode=%s missing=%s countdown=%s describe=%s/%s/%s sortedLetters=%s grid=%d".formatted(
                sum(1, 2, 3), block.lines().count(), mode, missing, countdown,
                describe("hey"), describe(41), describe(2.5), new String(letters), grid[1][0]);
    }
}
