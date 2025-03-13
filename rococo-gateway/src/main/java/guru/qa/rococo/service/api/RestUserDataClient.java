package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.UserJson;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * REST-клиент для работы с микросервисом пользовательских данных.
 * Обеспечивает получение и обновление информации о пользователях.
 */
@Component
public class RestUserDataClient {
    private final RestTemplate restTemplate;
    private final String rococoUserBaseUri;

    @Autowired
    public RestUserDataClient(RestTemplate restTemplate,
                              @Value("${rococo-userdata.base-uri}") String rococoUserBaseUri) {
        this.restTemplate = restTemplate;
        this.rococoUserBaseUri = rococoUserBaseUri + "/internal";
    }

    /**
     * Получает информацию о пользователе по имени.
     *
     * @param username Имя пользователя (не null)
     * @return Объект пользователя {@link UserJson}
     * @throws NoRestResponseException Если ответ от сервиса отсутствует
     */
    public @Nonnull UserJson getUser(@Nonnull String username) {
        // Формируем URI с параметром запроса username
        URI uri = UriComponentsBuilder
                .fromUriString(rococoUserBaseUri)
                .path("/user")
                .queryParam("username", username)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // Отправляем GET-запрос и получаем ответ
        ResponseEntity<UserJson> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                UserJson.class
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException(
                        "No REST response is given [/internal/user GET]"
                ));
    }

    /**
     * Обновляет информацию о пользователе.
     *
     * @param user Объект с обновленными данными пользователя (не null)
     * @return Обновленный объект пользователя {@link UserJson}
     * @throws NoRestResponseException Если ответ от сервиса отсутствует
     */
    public @Nonnull UserJson updateUserInfo(@Nonnull UserJson user) {
        // Формируем URI для обновления
        URI uri = UriComponentsBuilder
                .fromUriString(rococoUserBaseUri)
                .path("/user")
                .build()
                .toUri();

        // Устанавливаем заголовки и тело запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserJson> request = new HttpEntity<>(user, headers);

        // Отправляем PATCH-запрос
        ResponseEntity<UserJson> response = restTemplate.exchange(
                uri,
                HttpMethod.PATCH,
                request,
                UserJson.class
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException(
                        "No REST response is given [/internal/user PATCH]"
                ));
    }
}