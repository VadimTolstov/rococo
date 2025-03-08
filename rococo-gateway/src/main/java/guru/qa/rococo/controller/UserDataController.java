package guru.qa.rococo.controller;

import guru.qa.rococo.config.RococoGatewayServiceConfig;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.api.RestUserDataClient;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления данными пользователя.
 * <p>
 * Обеспечивает операции получения и обновления профиля пользователя.
 * Требует аутентификации через OAuth2. Интегрируется с микросервисом пользовательских данных.
 * </p>
 */
@RestController
@RequestMapping("/api/user")
@SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
public class UserDataController {

    private static final Logger LOG = LoggerFactory.getLogger(UserDataController.class);
    private final RestUserDataClient restUserDataClient;

    @Autowired
    public UserDataController(RestUserDataClient restUserDataClient) {
        this.restUserDataClient = restUserDataClient;
    }

    /**
     * Получение данных текущего аутентифицированного пользователя.
     * <p>
     * Извлекает имя пользователя из JWT токена и возвращает его профиль.
     * </p>
     *
     * @param principal JWT токен, автоматически внедряемый Spring Security
     * @return Полный профиль пользователя {@link UserJson}
     */
    @GetMapping
    public UserJson getUser(@AuthenticationPrincipal Jwt principal) {
        String username = principal.getClaim("sub");
        LOG.debug("Requesting current user data for: {}", username);
        return restUserDataClient.getUser(username);
    }

    /**
     * Обновление профиля пользователя.
     * <p>
     * Валидирует входные данные и гарантирует, что обновление выполняется только для текущего пользователя.
     * Добавляет имя пользователя из токена в DTO для предотвращения подмены.
     * </p>
     *
     * @param principal JWT токен аутентификации
     * @param user      Объект с обновленными данными пользователя
     * @return Обновленный профиль пользователя {@link UserJson}
     * @throws jakarta.validation.ConstraintViolationException При невалидных данных
     */
    @PatchMapping
    public UserJson updateUser(@AuthenticationPrincipal Jwt principal,
                               @Valid @RequestBody UserJson user) {
        String username = principal.getClaim("sub");
        LOG.info("Updating user profile for: {}", username);

        // Добавляем username из токена в DTO, чтобы предотвратить изменение чужого профиля
        return restUserDataClient.updateUserInfo(
                user.addUsername(username)
        );
    }
}