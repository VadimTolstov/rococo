package guru.qa.rococo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер для обработки запросов, связанных с аутентификацией пользователей.
 * Этот класс отвечает за перенаправление пользователей на страницу входа (login)
 * или на главную страницу фронтенда в зависимости от состояния сессии и предыдущих запросов.
 */
@Controller
public class LoginController {

  // Имя представления (view) для страницы входа в систему
  private static final String LOGIN_VIEW_NAME = "login";

  // Атрибут сессии, в котором Spring Security сохраняет информацию о предыдущем запросе
  private static final String PRE_REQ_ATTR = "SPRING_SECURITY_SAVED_REQUEST";

  // URI, который указывает на запрос авторизации OAuth2
  private static final String PRE_REQ_URI = "/oauth2/authorize";

  // URI фронтенд-приложения, используемый для перенаправления пользователя
  private final String rococoFrontUri;

  /**
   * Конструктор для внедрения значения из конфигурации.
   *
   * @param rococoFrontUri URI фронтенд-приложения, который указывает на главную страницу.
   *                        Значение берется из конфигурационного файла (например, application.properties).
   */
  public LoginController(@Value("${rococo-front.base-uri}") String rococoFrontUri) {
    this.rococoFrontUri = rococoFrontUri;
  }

  /**
   * Обрабатывает GET-запрос на /login.
   * Этот метод проверяет, был ли пользователь перенаправлен на страницу входа из-за попытки доступа
   * к защищенному ресурсу (например, через OAuth2). Если это так, пользователю показывается страница входа.
   * В противном случае пользователь перенаправляется на главную страницу фронтенда.
   *
   * @param session Объект HttpSession, который содержит информацию о текущей сессии пользователя.
   * @return Если сохраненный запрос соответствует ожидаемому, возвращает имя представления для страницы входа.
   *         В противном случае перенаправляет на главную страницу фронтенда.
   */
  @GetMapping("/login")
  public String login(HttpSession session) {
    // Получаем сохраненный запрос из сессии
    DefaultSavedRequest savedRequest = (DefaultSavedRequest) session.getAttribute(PRE_REQ_ATTR);

    // Проверяем, есть ли сохраненный запрос и соответствует ли его URI ожидаемому
    if (savedRequest == null || !savedRequest.getRequestURI().equals(PRE_REQ_URI)) {
      // Если сохраненный запрос отсутствует или его URI не соответствует ожидаемому,
      // перенаправляем пользователя на главную страницу фронтенда
      return "redirect:" + rococoFrontUri;
    }

    // Если сохраненный запрос соответствует ожидаемому, возвращаем имя представления для страницы входа
    return LOGIN_VIEW_NAME;
  }
}