package io.github.aachar0.inventoryapi.exception;

/**
 * Our own exception types. Services throw them; GlobalExceptionHandler turns each into the right HTTP status.
 * RuntimeException = "unchecked": callers are not forced to catch it, which keeps service code clean.
 */
public final class ApiExceptions {

    private ApiExceptions() {
    }

    /** 404: the thing asked for does not exist. */
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    /** 409: the request clashes with existing data (duplicate SKU, category still in use, ...). */
    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message);
        }
    }

    /** 409: not enough stock for an OUT movement. */
    public static class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(String sku, int available, int requested) {
            super("Not enough stock for " + sku + ": available " + available + ", requested " + requested);
        }
    }

    /** 401: wrong username or password. */
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    /** 400: the request is well-formed JSON but asks for something invalid (e.g. sort by an unknown field). */
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }
}
