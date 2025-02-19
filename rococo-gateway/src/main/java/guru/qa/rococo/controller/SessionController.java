package guru.qa.rococo.controller;

import guru.qa.rococo.model.SessionJson;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Контроллер для работы с сессиями пользователя.
 * <p>
 * Предоставляет информацию о текущей аутентифицированной сессии пользователя,
 * включая сроки действия токена доступа.
 * </p>
 */
@RestController
@RequestMapping("/api/session")
public class SessionController {

    /**
     * Возвращает информацию о текущей сессии пользователя.
     * <p>
     * Извлекает данные из JWT токена аутентификации:
     * </p>
     * <ul>
     *   <li><b>sub</b> - идентификатор пользователя (логин)</li>
     *   <li><b>issuedAt</b> - время выдачи токена</li>
     *   <li><b>expiresAt</b> - время истечения срока действия токена</li>
     * </ul>
     *
     * @param principal JWT токен аутентифицированного пользователя, автоматически внедряемый Spring Security
     * @return Объект сессии {@link SessionJson} или пустой объект если пользователь не аутентифицирован
     */
    @GetMapping
    public SessionJson session(@AuthenticationPrincipal Jwt principal) {
        if (principal != null) {
            return new SessionJson(
                    // Извлекаем логин пользователя из стандартного поля JWT "sub"
                    principal.getClaim("sub"),
                    // Конвертируем Instant в Date для времени выдачи токена
                    Date.from(principal.getIssuedAt()),
                    // Конвертируем Instant в Date для времени истечения токена
                    Date.from(principal.getExpiresAt())
            );
        } else {
            // Возвращаем пустую сессию если аутентификация отсутствует
            return SessionJson.empty();
        }
    }
}