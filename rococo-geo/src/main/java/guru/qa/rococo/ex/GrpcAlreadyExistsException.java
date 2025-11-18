package guru.qa.rococo.ex;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение для конфликтующих операций (например, дубликаты).
 * Соответствует gRPC Status.ALREADY_EXISTS
 */
public class GrpcAlreadyExistsException extends RuntimeException {
    public GrpcAlreadyExistsException(String message) {
        super(message);
    }

    public GrpcAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}