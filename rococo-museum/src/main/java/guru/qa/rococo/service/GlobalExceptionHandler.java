package guru.qa.rococo.service;


import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.ErrorJson;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
        return ResponseEntity
                .status(status)
                .body(new ErrorJson(
                        appName + ": Entity validation error", // Тип ошибки
                        HttpStatus.resolve(status.value()).getReasonPhrase(), // Стандартное описание статуса
                        status.value(), // HTTP-статус код
                        // Собираем сообщения об ошибках валидации в одну строку
                        ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.joining(", ")),
                        // URI запроса, вызвавшего ошибку
                        ((ServletWebRequest) request).getRequest().getRequestURI()
                ));
    }

    /**
     * Обрабатывает исключение BadRequestException и возвращает ответ с HTTP статусом 400 Bad Request.
     *
     * @param ex      перехваченное исключение, содержащее информацию об ошибке
     * @param request HTTP-запрос, в котором произошло исключение
     * @return ResponseEntity с объектом {@link ErrorJson} и статусом  {@link HttpStatus} BAD_REQUEST
     * @example Пример ответа:
     * {
     * "error": "Bad Request",
     * "message": "Invalid request parameters",
     * "path": "/api/endpoint"
     * }
     * @see ErrorJson
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorJson> handleBadRequestException(@Nonnull BadRequestException ex,
                                                               @Nonnull HttpServletRequest request) {
        LOG.warn("Bad request: {}", ex.getMessage());
        return withStatus(
                "Bad Request",
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    /**
     * Обрабатывает исключение NotFoundException и возвращает ответ с HTTP статусом 404 Not Found.
     *
     * @param ex      перехваченное исключение, содержащее информацию об ошибке
     * @param request HTTP-запрос, в котором произошло исключение
     * @return ResponseEntity с объектом {@link ErrorJson} и статусом  {@link HttpStatus} NOT_FOUND
     * @example Пример ответа:
     * {
     * "error": "Not Found",
     * "message": "User not found",
     * "path": "/api/user/123"
     * }
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorJson> handleNotFoundException(@Nonnull NotFoundException ex,
                                                             @Nonnull HttpServletRequest request) {
        LOG.warn("Resource not found: {}", ex.getMessage());
        return withStatus(
                "Not Found",
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    /**
     * Обрабатывает все остальные исключения, которые не были перехвачены другими обработчиками.
     *
     * @param ex      Исключение.
     * @param request HTTP-запрос.
     * @return Ответ с информацией об ошибке {@link ErrorJson}.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorJson> handleException(@Nonnull Exception ex,
                                                     @Nonnull HttpServletRequest request) {
        LOG.warn("### Resolve Exception in @RestControllerAdvice ", ex);
        return withStatus("Internal error", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    /**
     * Создает ответ с ошибкой в стандартном формате.
     *
     * @param type    Тип ошибки (для отображения в интерфейсе)
     * @param status  HTTP статус
     * @param message Детальное сообщение об ошибке
     * @param request HTTP запрос
     * @return Ответ с заполненным объектом {@link ErrorJson}.
     */
    private @Nonnull ResponseEntity<ErrorJson> withStatus(@Nonnull String type,
                                                          @Nonnull HttpStatus status,
                                                          @Nonnull String message,
                                                          @Nonnull HttpServletRequest request) {
        return ResponseEntity
                .status(status)
                .body(new ErrorJson(
                        appName + ": " + type, // Тип ошибки с именем приложения
                        status.getReasonPhrase(), // Стандартное описание статуса
                        status.value(), // HTTP-код
                        message, // Детализированное сообщение
                        request.getRequestURI() // URI запроса
                ));
    }


}