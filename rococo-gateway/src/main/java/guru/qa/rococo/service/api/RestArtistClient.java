package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.page.RestPage;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Клиент для взаимодействия с REST API сервиса художников.
 * <p>
 * Обеспечивает выполнение CRUD операций с художниками через внешнее API.
 * Автоматически добавляет базовый URI из конфигурации и обрабатывает ошибки.
 * </p>
 */
@Component
public class RestArtistClient {

    private final RestTemplate restTemplate;
    private final String rococoArtistBaseUri;

    /**
     * Конструктор клиента.
     *
     * @param restTemplate         экземпляр RestTemplate для HTTP запросов
     * @param rococoArtistBaseUri  базовый URI API художников из конфигурации
     */
    @Autowired
    public RestArtistClient(RestTemplate restTemplate,
                            @Value("${rococo-artist.base-uri}") String rococoArtistBaseUri) {
        this.restTemplate = restTemplate;
        this.rococoArtistBaseUri = rococoArtistBaseUri + "/internal";
    }

    /**
     * Получает страницу художников с пагинацией и сортировкой.
     *
     * @param pageable параметры пагинации и сортировки
     * @return страница художников {@link RestPage<ArtistJson>}
     * @throws NoRestResponseException если возникла ошибка при запросе или обработке ответа
     */
    public @Nonnull Page<ArtistJson> getAllArtists(@Nonnull Pageable pageable) {
        URI uri = buildPaginatedUri("/artist", pageable);
        return executeRequest(uri, HttpMethod.GET, null, new ParameterizedTypeReference<RestPage<ArtistJson>>() {});
    }

    /**
     * Получает художника по уникальному идентификатору.
     *
     * @param id UUID художника
     * @return объект художника {@link ArtistJson}
     * @throws NoRestResponseException если художник не найден или произошла ошибка запроса
     */
    public @Nonnull ArtistJson getArtistById(@Nonnull UUID id) {
        URI uri = buildUriWithPath("/artist/{id}", id);
        return executeRequest(uri, HttpMethod.GET, null, ArtistJson.class);
    }

    /**
     * Ищет художника по точному совпадению имени.
     *
     * @param name точное имя художника для поиска
     * @return объект художника {@link ArtistJson}
     * @throws NoRestResponseException если художник не найден или произошла ошибка запроса
     */
    public @Nonnull ArtistJson getArtistByName(@Nonnull String name) {
        URI uri = buildUriWithQuery("/artist", "name", name);
        return executeRequest(uri, HttpMethod.GET, null, ArtistJson.class);
    }

    /**
     * Создает нового художника.
     *
     * @param artist объект художника для создания
     * @return созданный объект художника {@link ArtistJson}
     * @throws NoRestResponseException если произошла ошибка при создании
     */
    public @Nonnull ArtistJson addArtist(@Nonnull ArtistJson artist) {
        URI uri = buildUri("/artist");
        return executeRequest(uri, HttpMethod.POST, artist, ArtistJson.class);
    }

    /**
     * Обновляет данные существующего художника.
     *
     * @param artist объект художника с обновленными данными
     * @return обновленный объект художника {@link ArtistJson}
     * @throws NoRestResponseException если произошла ошибка при обновлении
     */
    public @Nonnull ArtistJson updateArtist(@Nonnull ArtistJson artist) {
        URI uri = buildUri("/artist");
        return executeRequest(uri, HttpMethod.PATCH, artist, ArtistJson.class);
    }

    /**
     * Строит URI с параметрами пагинации и сортировки.
     *
     * @param path     базовый путь API
     * @param pageable параметры страницы и сортировки
     * @return полный URI с параметрами
     */
    private URI buildPaginatedUri(String path, Pageable pageable) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(rococoArtistBaseUri)
                .path(path)
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());

        // Добавляем параметры сортировки в формате "property,direction"
        pageable.getSort().forEach(order ->
                builder.queryParam("sort",
                        order.getProperty() + "," + order.getDirection().name().toLowerCase())
        );

        return builder.build().toUri();
    }

    /**
     * Строит URI с path-параметрами.
     *
     * @param path      шаблон пути с плейсхолдерами {param}
     * @param variables значения для подстановки в path
     * @return URI с подставленными значениями
     */
    private URI buildUriWithPath(String path, Object... variables) {
        return UriComponentsBuilder
                .fromUriString(rococoArtistBaseUri)
                .path(path)
                .buildAndExpand(variables)
                .toUri();
    }

    /**
     * Строит URI с query-параметром.
     *
     * @param path      базовый путь API
     * @param paramName имя query-параметра
     * @param paramValue значение query-параметра
     * @return URI с добавленным параметром
     */
    private URI buildUriWithQuery(String path, String paramName, String paramValue) {
        return UriComponentsBuilder
                .fromUriString(rococoArtistBaseUri)
                .path(path)
                .queryParam(paramName, paramValue)
                .build()
                .toUri();
    }

    /**
     * Строит базовый URI без дополнительных параметров.
     *
     * @param path конечный путь API
     * @return полный URI
     */
    private URI buildUri(String path) {
        return UriComponentsBuilder
                .fromUriString(rococoArtistBaseUri)
                .path(path)
                .build()
                .toUri();
    }

    /**
     * Выполняет HTTP запрос и обрабатывает ответ.
     *
     * @param uri          целевой URI
     * @param method       HTTP метод
     * @param body         тело запроса (может быть null)
     * @param responseType ожидаемый тип ответа
     * @param <T>          тип возвращаемого объекта
     * @return десериализованный ответ
     * @throws NoRestResponseException при ошибках запроса или невалидном ответе
     */
    private <T> T executeRequest(URI uri, HttpMethod method, Object body, Class<T> responseType) {
        return executeRequest(uri, method, body, ParameterizedTypeReference.forType(responseType));
    }

    /**
     * Выполняет HTTP запрос для Generic типов.
     *
     * @param uri          целевой URI
     * @param method       HTTP метод
     * @param body         тело запроса (может быть null)
     * @param responseType ParameterizedTypeReference для сложных типов
     * @param <T>          тип возвращаемого объекта
     * @return десериализованный ответ
     * @throws NoRestResponseException при ошибках запроса или невалидном ответе
     */
    private <T> T executeRequest(URI uri, HttpMethod method, Object body, ParameterizedTypeReference<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> request = new HttpEntity<>(body, headers);

            ResponseEntity<T> response = restTemplate.exchange(
                    uri,
                    method,
                    request,
                    responseType
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new NoRestResponseException(
                        "Request failed: %s %s. Status: %s".formatted(
                                method, uri.getPath(), response.getStatusCode()
                        ));
            }

            return Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new NoRestResponseException("Empty body in response"));

        } catch (RestClientException e) {
            throw new NoRestResponseException("API request failed: %s %s %s".formatted(method, uri, e));
        }
    }
}