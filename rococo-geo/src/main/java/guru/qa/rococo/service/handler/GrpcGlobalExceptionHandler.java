package guru.qa.rococo.service.handler;

import guru.qa.rococo.ex.GrpcAlreadyExistsException;
import guru.qa.rococo.ex.GrpcBadRequestException;
import guru.qa.rococo.ex.GrpcNotFoundException;
import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcAdvice
public class GrpcGlobalExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcGlobalExceptionHandler.class);

  /**
   * Обрабатывает ошибки валидации и невалидные аргументы.
   * Соответствует HTTP 400 Bad Request.
   *
   * @param ex перехваченное исключение
   * @return gRPC Status с кодом INVALID_ARGUMENT
   */
  @GrpcExceptionHandler(GrpcBadRequestException.class)
  public Status handleBadRequest(GrpcBadRequestException ex) {
    LOG.warn("Bad request in gRPC call: {}", ex.getMessage());
    return Status.INVALID_ARGUMENT
        .withDescription(ex.getMessage())
        .withCause(ex);
  }

  /**
   * Обрабатывает случаи, когда запрашиваемый ресурс не найден.
   * Соответствует HTTP 404 Not Found.
   *
   * @param ex перехваченное исключение
   * @return gRPC Status с кодом NOT_FOUND
   */
  @GrpcExceptionHandler(GrpcNotFoundException.class)
  public Status handleNotFound(GrpcNotFoundException ex) {
    LOG.warn("Resource not found in gRPC call: {}", ex.getMessage());
    return Status.NOT_FOUND
        .withDescription(ex.getMessage())
        .withCause(ex);
  }

  /**
   * Обрабатывает конфликты, когда ресурс уже существует.
   * Соответствует HTTP 409 Conflict.
   *
   * @param ex перехваченное исключение
   * @return gRPC Status с кодом ALREADY_EXISTS
   */
  @GrpcExceptionHandler(GrpcAlreadyExistsException.class)
  public Status handleAlreadyExists(GrpcAlreadyExistsException ex) {
    LOG.warn("Resource already exists in gRPC call: {}", ex.getMessage());
    return Status.ALREADY_EXISTS
        .withDescription(ex.getMessage())
        .withCause(ex);
  }

  /**
   * Обрабатывает стандартные IllegalArgumentException как невалидные аргументы.
   *
   * @param ex перехваченное исключение
   * @return gRPC Status с кодом INVALID_ARGUMENT
   */
  @GrpcExceptionHandler(IllegalArgumentException.class)
  public Status handleIllegalArgument(IllegalArgumentException ex) {
    LOG.warn("Invalid argument in gRPC call: {}", ex.getMessage());
    return Status.INVALID_ARGUMENT
        .withDescription(ex.getMessage())
        .withCause(ex);
  }

  /**
   * Обрабатывает стандартные IllegalStateException как не найденные ресурсы.
   *
   * @param ex перехваченное исключение
   * @return gRPC Status с кодом NOT_FOUND
   */
  @GrpcExceptionHandler(IllegalStateException.class)
  public Status handleIllegalState(IllegalStateException ex) {
    LOG.warn("Not found in gRPC call: {}", ex.getMessage());
    return Status.NOT_FOUND
        .withDescription(ex.getMessage())
        .withCause(ex);
  }

  /**
   * Обрабатывает все остальные исключения, которые не были перехвачены другими обработчиками.
   * Соответствует HTTP 500 Internal Server Error.
   *
   * @param ex перехваченное исключение
   * @return gRPC Status с кодом INTERNAL
   */
  @GrpcExceptionHandler(Exception.class)
  public Status handleGenericException(Exception ex) {
    LOG.error("Unexpected error in gRPC call", ex);
    return Status.INTERNAL
        .withDescription("Internal server error: " + ex.getMessage())
        .withCause(ex);
  }
}