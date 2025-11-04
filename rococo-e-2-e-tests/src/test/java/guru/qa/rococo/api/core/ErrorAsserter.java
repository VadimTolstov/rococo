package guru.qa.rococo.api.core;

import guru.qa.rococo.model.rest.ApiError;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import retrofit2.HttpException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface ErrorAsserter {

  /**
   * Основной метод для проверки ошибок API.
   * Выполняет полную валидацию ответа об ошибке и добавляет информацию в Allure отчет.
   *
   * @param statusCode ожидаемый HTTP статус код
   * @param ex         исключение HttpException для анализа
   * @param code       ожидаемый код ошибки API
   * @param message    ожидаемое сообщение об ошибке
   * @param domain     ожидаемый домен ошибки
   * @param errors     ожидаемые детальные сообщения об ошибках
   * @throws AssertionError если какие-либо проверки не пройдены
   */
  @Step("Assert error message")
  default void assertError(
      int statusCode,
      HttpException ex,
      String code,
      String message,
      String domain,
      String... errors
  ) {
    ApiError actual = ErrorParser.parseError(ex.response());
    codeAttachment(statusCode, ex.code());
    bodyAttachment(
        toAllureString(code, message, domain, errors),
        actual
    );

    assertEquals(statusCode, ex.code(), "HTTP status code not as expected");
    assertAll(
        // Проверяем код ошибки через геттер
        () -> assertEquals(code, actual.status(), "Error code mismatch"),
        // Проверяем сообщение через геттер
        () -> assertEquals(message, actual.detail(), "Error message mismatch"),
        // Проверяем домен через геттер
        () -> assertEquals(domain, actual.path(), "Error domain mismatch"),
        // Проверяем детальные ошибки
        () -> {
          Set<String> actualSet = new HashSet<>(actual.errors());
          Set<String> expectedSet = Arrays.stream(errors).collect(Collectors.toSet());
          assertEquals(expectedSet, actualSet, "Detailed errors mismatch");
        });
  }

  private void codeAttachment(
      int expectedCode,
      int actualCode) {
    String codeAttachment = String.format("Expected code: %s\nActual code: %s", expectedCode, actualCode);
    Allure.addAttachment("status code", codeAttachment);
  }

  private void bodyAttachment(
      String expectedBody,
      ApiError actual) {
    String actualBody = toAllureString(actual.status(), actual.detail(), actual.path(), actual.errors().toArray(String[]::new));
    String attachment = String.format("Expected body: %s\nActual body: %s", expectedBody, actualBody);
    Allure.addAttachment("error body", attachment);
  }

  private String toAllureString(
      String code,
      String message,
      String domain,
      String... errors) {
    return String.format("code=%s, message=%s, domain=%s, errors=%s",
        code, message, domain, Arrays.toString(errors));
  }
}