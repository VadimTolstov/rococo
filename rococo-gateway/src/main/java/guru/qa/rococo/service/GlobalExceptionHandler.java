package guru.qa.rococo.service;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.ErrorJson;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для REST контроллеров.
 * <p>
 * Обрабатывает исключения на уровне всего приложения и возвращает стандартизированные JSON-ответы.
 * Наследует функционал {@link ResponseEntityExceptionHandler} для обработки исключений Spring.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @Value("${spring.application.name}")
  private String appName;

  /**
   * Обрабатывает ошибки валидации входных параметров методов контроллера.
   *
   * @param ex      Исключение с информацией о нарушении валидации
   * @param headers Заголовки HTTP ответа
   * @param status  HTTP статус ответа
   * @param request Запрос
   * @return Ответ с детализированной информацией об ошибках валидации
   */
  @Override
  protected @Nonnull ResponseEntity<Object> handleMethodArgumentNotValid(@Nonnull MethodArgumentNotValidException ex,
                                                                         @Nonnull HttpHeaders headers,
                                                                         @Nonnull HttpStatusCode status,
                                                                         @Nonnull WebRequest request) {
    // Собираем все ошибки валидации в список
    List<String> validationErrors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(FieldError::getDefaultMessage)
        .collect(Collectors.toList());

    // Создаем расширенный ErrorJson с массивом ошибок
    ErrorJson errorResponse = new ErrorJson(
        appName + ": Validation error",
        HttpStatus.resolve(status.value()).getReasonPhrase(),
        status.value(),
        "Validation failed. Check 'errors' field for details", // общее сообщение
        validationErrors, // массив отдельных ошибок
        LocalDateTime.now().toString(),
        ((ServletWebRequest) request).getRequest().getRequestURI()
    );

    return ResponseEntity.status(status).body(errorResponse);
  }

  /**
   * Обрабатывает исключения REST клиента (4xx и 5xx ошибки).
   *
   * @param ex      Исключение, возникшее при вызове REST API
   * @param request HTTP запрос
   * @return Ответ с оригинальными параметрами ошибки из удаленного сервиса
   */
  @ExceptionHandler({
      HttpClientErrorException.NotFound.class,
      HttpClientErrorException.BadRequest.class,
      HttpClientErrorException.Conflict.class,
      HttpClientErrorException.NotAcceptable.class,
      HttpServerErrorException.InternalServerError.class,
      HttpServerErrorException.ServiceUnavailable.class
  })
  public ResponseEntity<ErrorJson> handleRestTemplateExceptions(@Nonnull HttpClientErrorException ex,
                                                                @Nonnull HttpServletRequest request) {
    LOG.warn("### REST Exception caught in Gateway: {}", ex.getMessage());
    return handleForwardedException(ex, request);
  }

  /**
   * Обрабатывает случаи отсутствия ответа от REST сервиса.
   *
   * @param ex      Исключение с информацией о проблеме
   * @param request HTTP запрос
   * @return Ответ со статусом 503 Service Unavailable
   */
  @ExceptionHandler(NoRestResponseException.class)
  public ResponseEntity<ErrorJson> handleApiNoResponseException(@Nonnull RuntimeException ex,
                                                                @Nonnull HttpServletRequest request) {
    LOG.warn("### No REST Response ", ex);
    return withEnhancedStatus("API Error", HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request);
  }

  /**
   * Перехватывает все непредвиденные исключения.
   *
   * @param ex      Пойманное исключение
   * @param request HTTP запрос
   * @return Ответ со статусом 500 Internal Server Error
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorJson> handleUnexpectedException(@Nonnull Exception ex,
                                                             @Nonnull HttpServletRequest request) {
    LOG.error("### Internal Server Error ", ex);
    return withEnhancedStatus("Internal Error", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
  }

  /**
   * Создает ответ с ошибкой в расширенном формате.
   *
   * @param type    Тип ошибки (для отображения в интерфейсе)
   * @param status  HTTP статус
   * @param message Детальное сообщение об ошибке
   * @param request HTTP запрос
   * @return Ответ с заполненным объектом ErrorJson
   */
  private @Nonnull ResponseEntity<ErrorJson> withEnhancedStatus(@Nonnull String type,
                                                                @Nonnull HttpStatus status,
                                                                @Nonnull String message,
                                                                @Nonnull HttpServletRequest request) {
    ErrorJson errorResponse = new ErrorJson(
        appName + ": " + type,
        status.getReasonPhrase(),
        status.value(),
        message,
        List.of(message), // помещаем сообщение также в массив errors
        LocalDateTime.now().toString(),
        request.getRequestURI()
    );

    return ResponseEntity.status(status).body(errorResponse);
  }

  /**
   * Обрабатывает исключения, полученные от удаленных сервисов.
   * Перенаправляет оригинальные параметры ошибки (статус, сообщение).
   *
   * @param ex      Исключение с ответом удаленного сервиса
   * @param request HTTP запрос
   * @return Ответ с параметрами оригинальной ошибки
   */
  @Nonnull
  private ResponseEntity<ErrorJson> handleForwardedException(@Nonnull HttpClientErrorException ex,
                                                             @Nonnull HttpServletRequest request) {
    // Десериализация тела ошибки из удаленного сервиса
    ErrorJson originalError = ex.getResponseBodyAs(ErrorJson.class);

    ErrorJson enhancedError = new ErrorJson(
        originalError.type(),
        originalError.title(),
        originalError.status(),
        originalError.detail(),
        List.of(originalError.detail()), // создаем массив из деталей ошибки
        LocalDateTime.now().toString(),
        request.getRequestURI()
    );

    return ResponseEntity.status(originalError.status()).body(enhancedError);
  }
}