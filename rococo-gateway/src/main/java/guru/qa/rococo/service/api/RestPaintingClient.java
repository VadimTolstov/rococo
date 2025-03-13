package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.model.page.RestPage;
import guru.qa.rococo.service.utils.HttpQueryPaginationAndSort;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

/**
 * REST-клиент для работы с микросервисом картин.
 * Обеспечивает управление данными о картинах.
 */
@Component
public class RestPaintingClient {
    private final RestTemplate restTemplate;
    private final String rococoPaintingBaseUri;

    @Autowired
    public RestPaintingClient(RestTemplate restTemplate, @Value("${rococo-painting.base-uri}") String rococoPaintingBaseUri) {
        this.restTemplate = restTemplate;
        this.rococoPaintingBaseUri = rococoPaintingBaseUri + "/internal";
    }

    /**
     * Получает страницу картин с фильтрацией по названию.
     *
     * @param pageable Параметры пагинации и сортировки
     * @param title    Фильтр по названию (может быть null)
     * @return Страница картин {@link Page<PaintingJson>}
     * @throws NoRestResponseException Если ответ от сервиса отсутствует
     */
    public @Nonnull Page<PaintingJson> getAllPaintings(@Nonnull Pageable pageable,
                                                       @Nullable String title) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(rococoPaintingBaseUri)
                .path("/painting")
                .queryParams(new HttpQueryPaginationAndSort(pageable).toQueryParams());

        if (StringUtils.hasText(title)) {
            uriBuilder.queryParam("title", title.trim());
        }

        URI uri = uriBuilder.encode(StandardCharsets.UTF_8).build().toUri();

        ResponseEntity<RestPage<PaintingJson>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting GET]"));
    }

    /**
     * Получает картину по уникальному идентификатору.
     *
     * @param id UUID картины
     * @return Объект картины {@link PaintingJson}
     * @throws NoRestResponseException Если картина не найдена
     */
    public @Nonnull PaintingJson getPaintingById(@Nonnull UUID id) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoPaintingBaseUri)
                .path("/painting/{id}")
                .buildAndExpand(id)
                .toUri();

        ResponseEntity<PaintingJson> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                PaintingJson.class
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting/{id} GET]"));
    }

    /**
     * Получение картин по ID автора с пагинацией
     *
     * @param id       UUID автора
     * @param pageable параметры пагинации
     * @return страница картин
     */
    public @Nonnull Page<PaintingJson> getPaintingsByAuthorId(@Nonnull UUID id, @Nonnull Pageable pageable) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoPaintingBaseUri)
                .path("/painting/author/{id}")
                .queryParams(new HttpQueryPaginationAndSort(pageable).toQueryParams())
                .buildAndExpand(id)
                .toUri();

        ResponseEntity<RestPage<PaintingJson>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting/author/{id} GET]"));
    }

    /**
     * Добавляет новую картину.
     *
     * @param painting Данные картины
     * @return Созданная картина {@link PaintingJson}
     * @throws NoRestResponseException Если сервис недоступен
     */
    public @Nonnull PaintingJson addPainting(@Nonnull PaintingJson painting) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoPaintingBaseUri)
                .path("/painting")
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaintingJson> request = new HttpEntity<>(painting, headers);

        ResponseEntity<PaintingJson> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                request,
                PaintingJson.class
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting POST]"));
    }

    /**
     * Обновление существующей картины
     *
     * @param painting обновленные данные
     * @return обновленный объект картины {@link PaintingJson}
     * @throws NoRestResponseException если картина не найдена или сервис недоступен
     */
    public @Nonnull PaintingJson updatePainting(@Nonnull PaintingJson painting) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoPaintingBaseUri)
                .path("/painting")
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaintingJson> request = new HttpEntity<>(painting, headers);

        ResponseEntity<PaintingJson> response = restTemplate.exchange(
                uri,
                HttpMethod.PATCH,
                request,
                PaintingJson.class
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/painting PATCH]"));
    }
}
