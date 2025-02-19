package guru.qa.rococo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для обработки ошибок, связанных с аутентификацией и другими исключениями.
 * Этот класс реализует интерфейс `ErrorController`, что позволяет ему обрабатывать
 * все ошибки, возникающие в приложении, и отображать соответствующую страницу ошибки.
 */
@Controller
public class ErrorAuthController implements ErrorController {

  // Имя представления (view) для страницы ошибки
  private static final String ERROR_VIEW_NAME = "error";

  // URI фронтенд-приложения, используемый для перенаправления
  private final String rococoFrontUri;

  /**
   * Конструктор для внедрения значения из конфигурации.
   *
   * @param rococoFrontUri URI фронтенд-приложения, который указывает на главную страницу.
   *                        Значение берется из конфигурационного файла (например, application.properties).
   */
  public ErrorAuthController(@Value("${rococo-front.base-uri}") String rococoFrontUri) {
    this.rococoFrontUri = rococoFrontUri;
  }

  /**
   * Обрабатывает GET-запрос на /error.
   * Этот метод обрабатывает все ошибки, возникающие в приложении, и передает информацию
   * об ошибке (статус, сообщение) в модель для отображения на странице ошибки.
   *
   * @param request  Объект HttpServletRequest, который содержит информацию о запросе.
   * @param response Объект HttpServletResponse, который содержит информацию о статусе ответа.
   * @param model    Объект модели, который используется для передачи данных в представление.
   * @return Имя представления для страницы ошибки.
   */
  @GetMapping("/error")
  public String error(HttpServletRequest request, HttpServletResponse response, Model model) {
    // Получаем статус ошибки из ответа
    int status = response.getStatus();

    // Получаем исключение, которое вызвало ошибку (если есть)
    Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");

    // Добавляем статус ошибки в модель
    model.addAttribute("status", status);

    // Добавляем URI фронтенд-приложения в модель
    model.addAttribute("frontUri", rococoFrontUri + "/main");

    // Добавляем сообщение об ошибке в модель
    model.addAttribute("error", throwable != null ? throwable.getMessage() : "Unknown error");

    // Возвращаем имя представления для страницы ошибки
    return ERROR_VIEW_NAME;
  }
}
